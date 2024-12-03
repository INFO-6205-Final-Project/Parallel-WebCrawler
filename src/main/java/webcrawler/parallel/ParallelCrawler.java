package webcrawler.parallel;

import edu.neu.coe.info6205.util.LazyLogger;
import webcrawler.DTO.CrawlResultDTO;
import webcrawler.repository.GraphRepository;
import webcrawler.service.CrawlerService;
import webcrawler.service.GraphService;
import webcrawler.util.HttpUtils;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

/**
 * 并行爬虫逻辑，使用队列管理并行任务。
 */
public class ParallelCrawler {

    private static final LazyLogger logger = new LazyLogger(ParallelCrawler.class);

    private final ExecutorService executorService; // main thread poll
    private final ExecutorService asyncExecutor; // crawler thread poll
    private final CrawlerService crawlerService;
//    private final Set<String> visitedUrls;
    private final int maxDepth;
    private final Queue<UrlDepthPair> urlQueue; //
    private volatile boolean isStopped = false;
    private final AtomicInteger crawlCount = new AtomicInteger(0); // counter
    private int emptyQueueCount = 0;
    private static final int MAX_EMPTY_COUNT = 20; // max empty queue request

    /**
     * Make sure all threads are terminated and isStopped is true
     * @return true if all threads are stop
     */
    public boolean getIsStopped() {
        return executorService.isTerminated() && asyncExecutor.isTerminated() && isStopped;
    }


    /**
     * Constructor
     * @param threadCount thread num for Thread Pool manager
     * @param maxDepth set max depth
     */
    public ParallelCrawler(int threadCount, int maxDepth) {

        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.asyncExecutor = Executors.newFixedThreadPool(threadCount); // bigger than the main thread pool
        this.crawlerService = new CrawlerService();
        this.maxDepth = maxDepth;
        // parallel safe type set, serve as priority queue
//        this.visitedUrls = ConcurrentHashMap.newKeySet();
//        this.urlQueue = new ConcurrentLinkedQueue<>();

        this.urlQueue = new PriorityBlockingQueue<>();
    }

    /**
     * getter
     * @return Number of websites that have been crawled
     */
    public int getCrawlCount() {
        return crawlCount.get();
    }


    /**
     * start the crawler mission
     *
     * @param startUrls URL set
     */
    public void startCrawling(Set<String> startUrls) {
        for (String url : startUrls) {
            enqueueUrl(url, 0);
        }

//        // warmup
//        Set<CrawlResultDTO> urlsDTO = new HashSet<>();
//        for (String url : startUrls) {
//            urlsDTO.add(crawlerService.crawl(url));
//        }
//        for(CrawlResultDTO data : urlsDTO) {
//            for(String url : data.getExtractedUrls()) {
//                enqueueUrl(url, 1);
//            }
//        }

        int consumerNum = ((ThreadPoolExecutor) executorService).getCorePoolSize();
        System.out.println("consumer num" + consumerNum);
        for (int i = 0; i < consumerNum; i++) {
            executorService.submit(this::processQueue);
        }
    }

    /**
     * stop Crawling and wait for job to stop within 60s
     */
    public void stopCrawling() {
        isStopped = true;
        executorService.shutdown();
        asyncExecutor.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!asyncExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error( "Error occurred while stopping crawling: " + e.getMessage(), e);
        }
        System.out.println("Crawling stopped. All tasks completed.");
    }

    /**
     * add URL to the Queue
     *
     * @param url   URL
     * @param depth current depth
     */
    private void enqueueUrl(String url, int depth) {
//        System.out.println(Thread.currentThread().getName() + " AM I blocked the program?");
        if (depth > maxDepth) return;

        // unblock way
        if (!urlQueue.offer(new UrlDepthPair(url, depth))) {
//            System.err.println("Queue is full, cannot add URL: " + url);
            logger.warn("Queue is full, cannot add URL: " + url);

        } else {
//            System.out.println(Thread.currentThread().getName() + "add url: " + url);
        }
    }

    /**
     * processQueue
     */
    private void processQueue() {
        while (!isStopped ) {// && !Thread.currentThread().isInterrupted()
            try {
                UrlDepthPair pair = urlQueue.poll(); //take() ; poll(1, java.util.concurrent.TimeUnit.MINUTES)
                 // take(): use the block way to get elements, not null
                if (pair == null) {
                    emptyQueueCount++;
//                    System.out.println(Thread.currentThread().getName() + " - No tasks in the queue. Waiting for new tasks...sleep 1 s");

                    if (emptyQueueCount >= MAX_EMPTY_COUNT) {
//                        System.out.println(Thread.currentThread().getName() + " - Reached max empty count. Stopping crawler...");
                        stopCrawling();

                        logger.info( "Crawler stopped due to empty queue.");
                        logger.info( "Total crawled so far: " + getCrawlCount());
                        System.out.println("Crawler stopped");

                        System.out.println("Total crawled so far: " + getCrawlCount());
                        break;
                    }

                    sleep(1000);
                    continue;
                }
                // reset counter
                emptyQueueCount = 0;
                String url = pair.getUrl();
                int depth = pair.getDepth();

//                System.out.println(Thread.currentThread().getName() + " - Processing URL: " + url + " at depth " + depth);

                // synchronous crawl and store
                // crawl
                CompletableFuture.supplyAsync(() -> crawlerService.crawl(url), asyncExecutor)
                        .thenAccept(result -> {
                            if (result != null) {
                                crawlCount.incrementAndGet();
//                                System.out.println(Thread.currentThread().getName() + " - Processing crawl result for: " + url);
                                if(result.getExtractedUrls() != null) {
                                    result.getExtractedUrls()
                                            .forEach(link -> enqueueUrl(link, depth + 1)); // add new URL
                                    result.getExtractedUrls()
                                            .forEach(link -> crawlerService.storeData(result.getUrl(), link, result.getTitle(), result.getCrawlTime()));
//                                    System.out.println(Thread.currentThread().getName() + "Queue size after enqueue: " + urlQueue.size());
                                } else {
//                                    System.out.println(Thread.currentThread().getName() + "No sub URL found from " + result.getUrl());
                                    logger.warn(Thread.currentThread().getName() + "No sub URL found from " + result.getUrl());
                                }
                            } else {
//                                System.out.println(Thread.currentThread().getName() + " - No result for: " + url);
                                logger.warn(Thread.currentThread().getName() + " - No result for: " + url);
                            }
                        })
                        .exceptionally(ex -> {
//                            System.err.println(Thread.currentThread().getName() + " - Error processing URL: " + url + ", " + ex.getMessage());
                            logger.error(Thread.currentThread().getName() + " - Error processing URL: " + url + ", " + ex.getMessage());
                            return null;
                        });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
//                System.out.println("Thread interrupted. Exiting...");
                logger.warn("Thread interrupted. Exiting...");
                break;
            }
        }
    }

    /**
     * inner class
     */
    private static class UrlDepthPair implements Comparable<UrlDepthPair>{
        private final String url;
        private final int depth;

        public UrlDepthPair(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        public String getUrl() {
            return url;
        }

        public int getDepth() {
            return depth;
        }

        @Override
        public int compareTo(UrlDepthPair o) {
            int depthComp = Integer.compare(this.depth, o.depth);
            if(depthComp != 0) {
                return depthComp;
            }
            return Integer.compare(this.url.length(), o.url.length());
        }
    }
}

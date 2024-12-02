package webcrawler.parallel;

import webcrawler.DTO.CrawlResultDTO;
import webcrawler.repository.GraphRepository;
import webcrawler.service.CrawlerService;
import webcrawler.service.GraphService;
import webcrawler.util.HttpUtils;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

/**
 * 并行爬虫逻辑，使用队列管理并行任务。
 */
public class ParallelCrawler {
    private final ExecutorService executorService; // main thread poll
    private final ExecutorService asyncExecutor; // crawler thread poll
    private final CrawlerService crawlerService;
//    private final Set<String> visitedUrls;
    private final int maxDepth;
    private final Queue<UrlDepthPair> urlQueue; //
    private volatile boolean isStopped = false;


    /**
     * Constructor
     * @param threadCount thread num for Thread Pool manager
     * @param maxDepth set max depth
     */
    public ParallelCrawler(int threadCount, int maxDepth) {

        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.asyncExecutor = Executors.newFixedThreadPool(threadCount);
        this.crawlerService = new CrawlerService();
        this.maxDepth = maxDepth;
        // parallel safe type set, serve as priority queue
//        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.urlQueue = new ConcurrentLinkedQueue<>();
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
//        stopCrawling();
    }

    /**
     * stop Crawling and wait for job to stop within 60s
     */
    public void stopCrawling() {
        isStopped = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * add URL to the Queue
     *
     * @param url   URL
     * @param depth current depth
     */
    private void enqueueUrl(String url, int depth) {
        System.out.println(Thread.currentThread().getName() + " AM I blocked the program?");
        if (depth > maxDepth) return;
//        if (visitedUrls.contains(url)) return;

        // put new URL into current safe queue, if Queue is full, interrupt current thread, not terminate
//        try{
//            urlQueue.put(new UrlDepthPair(url, depth)); // block way
//        } catch (InterruptedException e) {
//            System.out.println("Queue is full for now");
//            Thread.currentThread().interrupt();
//        }
        // unblock way
        if (!urlQueue.offer(new UrlDepthPair(url, depth))) {
            System.err.println("Queue is full, cannot add URL: " + url);
        } else {
            System.out.println(Thread.currentThread().getName() + "add url: " + url);
        }
    }

    /**
     * 处理队列中的URL
     */
    private void processQueue() {
        while (!isStopped ) {// && !Thread.currentThread().isInterrupted()
            try {
                UrlDepthPair pair = urlQueue.poll(); //take() ; poll(1, java.util.concurrent.TimeUnit.MINUTES)
                 // take(): use the block way to get elements, not null
                if (pair == null) {
                    if (isStopped && urlQueue.isEmpty()) {
                        System.out.println(Thread.currentThread().getName() + " - Queue is empty and crawling is stopped. Thread exiting.");
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " - No tasks in the queue. Waiting for new tasks...sleep 1 s");
                    sleep(1000);
                    continue;
                }
                String url = pair.getUrl();
                int depth = pair.getDepth();

                System.out.println(Thread.currentThread().getName() + " - Processing URL: " + url + " at depth " + depth);

                // synchronous crawl and store
                // crawl
                CompletableFuture.supplyAsync(() -> crawlerService.crawl(url), asyncExecutor)
                        .thenAccept(result -> {
                            if (result != null) {
                                System.out.println(Thread.currentThread().getName() + " - Processing crawl result for: " + url);
                                if(result.getExtractedUrls() != null) {
                                    result.getExtractedUrls()
                                            .forEach(link -> enqueueUrl(link, depth + 1)); // add new URL
                                    result.getExtractedUrls()
                                            .forEach(link -> crawlerService.storeData(result.getUrl(), link, result.getTitle(), result.getCrawlTime()));
                                    System.out.println(Thread.currentThread().getName() + "Queue size after enqueue: " + urlQueue.size());
                                } else {
                                    System.out.println(Thread.currentThread().getName() + "No sub URL found from " + result.getUrl());
                                }
                            } else {
                                System.out.println(Thread.currentThread().getName() + " - No result for: " + url);
                            }
                        })
                        .exceptionally(ex -> {
                            System.err.println(Thread.currentThread().getName() + " - Error processing URL: " + url + ", " + ex.getMessage());
                            return null;
                        });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted. Exiting...");
                break;
            }
        }
    }

    /**
     * inner class
     */
    private static class UrlDepthPair {
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
    }
}

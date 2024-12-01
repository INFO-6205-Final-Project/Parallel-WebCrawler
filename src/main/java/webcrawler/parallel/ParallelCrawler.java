package webcrawler.parallel;

//<<<<<<< HEAD
//import webcrawler.service.CrawlerService;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//public class ParallelCrawler {
//
//    private final CrawlerService crawlerService;
//    private final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();
//
//    public ParallelCrawler(CrawlerService crawlerService) {
//        this.crawlerService = crawlerService;
//    }
//
//    /**
//     * 并行爬取 URL
//     * @param startUrls 起始 URL 列表
//     */
//    public void crawlInParallel(List<String> startUrls) {
//        urlQueue.addAll(startUrls);
//        List<CompletableFuture<Void>> tasks = new ArrayList<>();
//
//        // 并行处理队列中的任务
//        while (!urlQueue.isEmpty()) {
//            String currentUrl = urlQueue.poll();
//            if (currentUrl != null) {
//                tasks.add(CompletableFuture.runAsync(() -> processUrl(currentUrl)));
//            }
//        }
//
//        // 等待所有任务完成
//        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
//    }
//
//    /**
//     * run the crawler job as an atomic operation
//     * @param url  URL
//     */
//    private void processUrl(String url) {
//        // use crawlerService
//        Set<String> newUrls = crawlerService.crawl(url);
//
//        // add new url to Queue
//        urlQueue.addAll(newUrls);
//    }
//}
//=======
import webcrawler.service.CrawlerService;
import webcrawler.service.GraphService;

import java.util.Set;
import java.util.concurrent.*;

/**
 * 并行爬虫逻辑，使用队列管理并行任务。
 */
public class ParallelCrawler {
    private final ExecutorService executorService;
    private final GraphService graphService;
    private final CrawlerService crawlerService;
    private final Set<String> visitedUrls;
    private final int maxDepth;
    private final BlockingQueue<UrlDepthPair> urlQueue;
    private volatile boolean isStopped = false;


    /**
     * 构造函数
     */
    public ParallelCrawler(CrawlerService crawlerService, int i) {

        this.executorService = Executors.newFixedThreadPool(10);;
        this.graphService = new GraphService();
        this.crawlerService = new CrawlerService();
        this.maxDepth = 5;
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.urlQueue = new LinkedBlockingQueue<>();
    }

    /**
     * 启动爬虫，传入起始URL集合
     *
     * @param startUrls 起始URL集合
     */
    public void startCrawling(Set<String> startUrls) {
        // 将起始URL加入队列
        for (String url : startUrls) {
            enqueueUrl(url, 0);
        }

        // 启动消费者线程
        int consumers = ((webcrawler.parallel.ThreadPoolManager) webcrawler.parallel.ThreadPoolManager.getInstance(100)).getExecutorService().toString().length(); // 示例获取线程数
        for (int i = 0; i < ((webcrawler.parallel.ThreadPoolManager) webcrawler.parallel.ThreadPoolManager.getInstance(100)).getExecutorService().toString().length(); i++) {
            executorService.submit(this::processQueue);
        }
    }

    /**
     * 停止爬虫
     */
    public void stopCrawling() {
        isStopped = true;
    }

    /**
     * 将URL和其深度加入队列
     *
     * @param url   要爬取的URL
     * @param depth 当前深度
     */
    private void enqueueUrl(String url, int depth) {
        if (depth > maxDepth) return;
        if (visitedUrls.contains(url)) return;
        urlQueue.offer(new UrlDepthPair(url, depth));
    }

    /**
     * 处理队列中的URL
     */
    private void processQueue() {
        while (!isStopped) {
            try {
                UrlDepthPair pair = urlQueue.poll(1, java.util.concurrent.TimeUnit.MINUTES);
                if (pair == null) {
                    // 队列超时，可能没有更多任务
                    break;
                }
                String url = pair.getUrl();
                int depth = pair.getDepth();

                if (!visitedUrls.add(url)) {
                    continue; // 已访问
                }

                CompletableFuture.supplyAsync(() -> crawlerService.crawl(url), executorService)
//                        .thenApplyAsync(html -> {
//                            if (html != null) {
//                                return HttpUtils.extractLinks(html, url);
//                            }
//                            return Collections.<String>emptySet();
//                        }, executorService)
//                        .thenAcceptAsync(links -> {
//                            // 存储节点和边到图数据库
//                            graphService.saveNode(new Node(url));
//                            for (String link : links) {
//                                graphService.saveEdge(new Edge(url, link));
//                                // 将新链接加入队列
//                                enqueueUrl(link, depth + 1);
//                            }
//                        }, executorService)
                        .exceptionally(ex -> {
                            // 记录异常日志
                            System.err.println("Error crawling URL " + url + ": " + ex.getMessage());
                            return null;
                        });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 内部类，用于存储URL和其对应的爬取深度
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

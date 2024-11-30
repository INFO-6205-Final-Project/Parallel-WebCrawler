package webcrawler.parallel;

//<<<<<<< HEAD
import webcrawler.service.CrawlerService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webcrawler.model.Node;
import webcrawler.model.Edge;
import webcrawler.service.GraphService;
import webcrawler.util.HttpUtils;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * 广度优先并行爬虫逻辑，使用优先级队列和双队列管理层级。
 */
public class ParallelCrawlerSingle {
    private static final Logger logger = LoggerFactory.getLogger(ParallelCrawlerSingle.class);
    private final CrawlerService crawlerService;
    private final PriorityBlockingQueue<URLWithPriority> urlQueue;
    private final ThreadPoolManager threadPoolManager;
    private final ExecutorService executor;

    /**
     * 构造函数，初始化并行爬虫。
     *
     * @param crawlerService 爬虫服务实例
     * @param threads        线程池大小
     */
    public ParallelCrawlerSingle(CrawlerService crawlerService, int threads) {
        this.crawlerService = crawlerService;
        // 定义优先级队列，优先级高的URL先被处理
        this.urlQueue = new PriorityBlockingQueue<>(100, Comparator.comparingInt(URLWithPriority::getPriority).reversed());
        this.threadPoolManager = ThreadPoolManager.getInstance(threads);
        this.executor = threadPoolManager.getExecutor();
    }

    /**
     * 新的构造函数，使用默认线程池大小
     *
     * @param crawlerService 爬虫服务实例
     */
    public ParallelCrawlerSingle(CrawlerService crawlerService) {
        this(crawlerService, 10); // 默认线程池大小为10
    }

    /**
     * 并行广度优先爬取 URL
     *
     * @param startUrls 起始 URL 列表
     */
    public void crawlInParallel(List<String> startUrls) {
        // 初始化URL队列
        for (String url : startUrls) {
            urlQueue.add(new URLWithPriority(url, computePriority(url)));
        }

        int depth = 0;

        while (!urlQueue.isEmpty()) {
            depth++;
            logger.info("Crawling depth: {}", depth);
            List<CompletableFuture<Void>> tasks = new ArrayList<>();
            int currentLevelSize = urlQueue.size();

            // 使用CountDownLatch等待当前层所有任务完成
            CountDownLatch latch = new CountDownLatch(currentLevelSize);

            for (int i = 0; i < currentLevelSize; i++) {
                URLWithPriority urlWithPriority = urlQueue.poll();
                if (urlWithPriority != null) {
                    CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                        try {
                            String currentUrl = urlWithPriority.getUrl();
                            Set<String> newUrls = crawlerService.crawl(currentUrl);
                            crawlerService.saveData(currentUrl, newUrls);
                            for (String newUrl : newUrls) {
                                if (isValid(newUrl)) {
                                    urlQueue.add(new URLWithPriority(newUrl, computePriority(newUrl)));
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error processing URL: {}", urlWithPriority.getUrl(), e);
                        } finally {
                            latch.countDown();
                        }
                    }, executor);
                    tasks.add(task);
                }
            }

            try {
                latch.await(); // 等待当前层所有任务完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Crawling interrupted: {}", e.getMessage());
                break;
            }
        }

        // 关闭线程池
        threadPoolManager.shutdown();
        logger.info("Crawling completed.");
    }

    /**
     * 定义启发式优先级计算方法
     *
     * @param url 目标URL
     * @return 优先级值（数值越高，优先级越高）
     */
    private int computePriority(String url) {
        // 示例启发式方法：
        // 1. 包含"important"的URL优先级更高
        // 2. 不处理暗网（包含"darkweb"的URL）
        if (url.contains("important")) {
            return 10;
        } else if (url.contains("darkweb")) {
            return -1; // -1表示不加入队列
        } else {
            return 1;
        }
    }

    /**
     * 检查URL是否有效（根据启发式规则）
     *
     * @param url 目标URL
     * @return 是否有效
     */
    private boolean isValid(String url) {
        // 忽略启发式计算中被标记为不处理的URL
        int priority = computePriority(url);
        return priority > 0;
    }

    /**
     * 用于封装URL和优先级
     */
    private static class URLWithPriority {
        private final String url;
        private final int priority;

        public URLWithPriority(String url, int priority) {
            this.url = url;
            this.priority = priority;
        }

        public String getUrl() {
            return url;
        }

        public int getPriority() {
            return priority;
        }
    }
}
//=======
//import webcrawler.model.Node;
//import webcrawler.model.Edge;
//import webcrawler.service.GraphService;
//import webcrawler.util.HttpUtils;
//
//import java.net.URL;
//import java.util.Collections;
//import java.util.Set;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.LinkedBlockingQueue;
//
///**
// * 并行爬虫逻辑，使用队列管理并行任务。
// */
//public class ParallelCrawler {
//    private final ExecutorService executorService;
//    private final GraphService graphService;
//    private final Set<String> visitedUrls;
//    private final int maxDepth;
//    private final BlockingQueue<UrlDepthPair> urlQueue;
//    private volatile boolean isStopped = false;
//
//    /**
//     * 构造函数
//     *
//     * @param executorService 执行器服务
//     * @param graphService    图数据库服务
//     * @param maxDepth        最大爬取深度
//     */
//    public ParallelCrawler(ExecutorService executorService, GraphService graphService, int maxDepth) {
//        this.executorService = executorService;
//        this.graphService = graphService;
//        this.maxDepth = maxDepth;
//        this.visitedUrls = ConcurrentHashMap.newKeySet();
//        this.urlQueue = new LinkedBlockingQueue<>();
//    }
//
//    /**
//     * 启动爬虫，传入起始URL集合
//     *
//     * @param startUrls 起始URL集合
//     */
//    public void startCrawling(Set<String> startUrls) {
//        // 将起始URL加入队列
//        for (String url : startUrls) {
//            enqueueUrl(url, 0);
//        }
//
//        // 启动消费者线程
//        int consumers = ((webcrawler.parallel.ThreadPoolManager) webcrawler.parallel.ThreadPoolManager.getInstance(100)).getExecutorService().toString().length(); // 示例获取线程数
//        for (int i = 0; i < ((webcrawler.parallel.ThreadPoolManager) webcrawler.parallel.ThreadPoolManager.getInstance(100)).getExecutorService().toString().length(); i++) {
//            executorService.submit(this::processQueue);
//        }
//    }
//
//    /**
//     * 停止爬虫
//     */
//    public void stopCrawling() {
//        isStopped = true;
//    }
//
//    /**
//     * 将URL和其深度加入队列
//     *
//     * @param url   要爬取的URL
//     * @param depth 当前深度
//     */
//    private void enqueueUrl(String url, int depth) {
//        if (depth > maxDepth) return;
//        if (visitedUrls.contains(url)) return;
//        urlQueue.offer(new UrlDepthPair(url, depth));
//    }
//
//    /**
//     * 处理队列中的URL
//     */
//    private void processQueue() {
//        while (!isStopped) {
//            try {
//                UrlDepthPair pair = urlQueue.poll(1, java.util.concurrent.TimeUnit.MINUTES);
//                if (pair == null) {
//                    // 队列超时，可能没有更多任务
//                    break;
//                }
//                String url = pair.getUrl();
//                int depth = pair.getDepth();
//
//                if (!visitedUrls.add(url)) {
//                    continue; // 已访问
//                }
//
//                CompletableFuture.supplyAsync(() -> HttpUtils.fetchContent(url), executorService)
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
//                        .exceptionally(ex -> {
//                            // 记录异常日志
//                            System.err.println("Error crawling URL " + url + ": " + ex.getMessage());
//                            return null;
//                        });
//
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
//
//    /**
//     * 内部类，用于存储URL和其对应的爬取深度
//     */
//    private static class UrlDepthPair {
//        private final String url;
//        private final int depth;
//
//        public UrlDepthPair(String url, int depth) {
//            this.url = url;
//            this.depth = depth;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public int getDepth() {
//            return depth;
//        }
//    }
//}

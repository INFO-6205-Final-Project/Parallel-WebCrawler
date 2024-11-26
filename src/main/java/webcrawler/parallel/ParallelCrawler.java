package webcrawler.parallel;

import webcrawler.service.CrawlerService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParallelCrawler {

    private final CrawlerService crawlerService;
    private final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();

    public ParallelCrawler(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    /**
     * 并行爬取 URL
     * @param startUrls 起始 URL 列表
     */
    public void crawlInParallel(List<String> startUrls) {
        urlQueue.addAll(startUrls);
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        // 并行处理队列中的任务
        while (!urlQueue.isEmpty()) {
            String currentUrl = urlQueue.poll();
            if (currentUrl != null) {
                tasks.add(CompletableFuture.runAsync(() -> processUrl(currentUrl)));
            }
        }

        // 等待所有任务完成
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
    }

    /**
     * run the crawler job as an atomic operation
     * @param url  URL
     */
    private void processUrl(String url) {
        // use crawlerService
        Set<String> newUrls = crawlerService.crawl(url);

        // add new url to Queue
        urlQueue.addAll(newUrls);
    }
}
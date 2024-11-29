package webcrawler.controller;

import webcrawler.parallel.ParallelCrawlerSingle;
import webcrawler.service.CrawlerService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlerController {
    private final ParallelCrawlerSingle parallelCrawlerSingle;
    private final CrawlerService crawlerService;

    public CrawlerController() {
        // init the crawler service and parallel service
        this.crawlerService = new CrawlerService();
        this.parallelCrawlerSingle = new ParallelCrawlerSingle(crawlerService);
    }

    /**
     * start crawler
     * @param startUrls init URL list
     */
    public void startCrawling(List<String> startUrls) {
        System.out.println("Starting the web crawler...");
        Set<String> urls = new HashSet<>();
        for (String url : startUrls) {
            urls = crawlerService.crawl(url);
        }
        System.out.println("Crawling completed.");
        for (String url : urls) {
            System.out.println(url);
        }
    }

    public static void main(String[] args) {
        CrawlerController controller = new CrawlerController();

        // 测试爬虫任务
        List<String> testUrls = List.of(
                "https://www.cfainstitute.org/insights/professional-learning"
        );
        controller.startCrawling(testUrls);
    }
}

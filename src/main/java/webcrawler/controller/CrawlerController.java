package webcrawler.controller;

import webcrawler.DTO.CrawlResultDTO;
import webcrawler.parallel.ParallelCrawler;
import webcrawler.service.CrawlerService;

import webcrawler.model.Edge;
import webcrawler.model.Node;
import webcrawler.repository.GraphRepository;

import java.util.List;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlerController {
    private final ParallelCrawler parallelCrawler;
    private final CrawlerService crawlerService;

    public CrawlerController(int threadCount, int maxDepth) {
        // init the crawler service and parallel service
        this.crawlerService = new CrawlerService();
        this.parallelCrawler = new ParallelCrawler(threadCount, maxDepth);

    }

    /**
     * start crawler
     * @param startUrls init URL list
     */
    public void startCrawling(List<String> startUrls) {
        System.out.println("Starting the web crawler...");
        Set<CrawlResultDTO> urls = new HashSet<>();
        for (String url : startUrls) {
            urls.add(crawlerService.crawl(url));
        }
        System.out.println("Crawling completed.");
        for (CrawlResultDTO url : urls) {
            System.out.println(url);
            for(String suburl : url.getExtractedUrls()) {
                crawlerService.storeData(url.getUrl(), suburl, url.getTitle(), url.getCrawlTime());
            }
        }

    }

    public static void main(String[] args) {
        CrawlerController controller = new CrawlerController(4, 3);

        // 测试爬虫任务
        Set<String> testUrls = Set.of(
                "https://www.cfainstitute.org/insights/professional-learning"
        );
        controller.parallelCrawler.startCrawling(testUrls);


        System.out.println("Crawler stopped");

        controller.crawlerService.pageRank();

        //Out-degree
        GraphRepository repository = new GraphRepository("bolt://localhost:7687", "neo4j", "12345678");
        try {
            repository.printNodeOutDegrees();
        } finally {
            repository.close();
        }



//        CrawlerController controller = new CrawlerController(10,3);
//
//        // 测试爬虫任务
//        List<String> testUrls = List.of(
//                "https://www.cfainstitute.org/insights/professional-learning"
//        );
//        controller.startCrawling(testUrls);



    }
}

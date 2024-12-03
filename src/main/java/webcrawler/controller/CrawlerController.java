package webcrawler.controller;

import webcrawler.DTO.CrawlResultDTO;
import webcrawler.parallel.ParallelCrawler;
import webcrawler.service.CrawlerService;
import webcrawler.util.Benchmark;

import webcrawler.model.Edge;
import webcrawler.model.Node;
import webcrawler.repository.GraphRepository;

import java.util.List;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import edu.neu.coe.info6205.util.Benchmark_Timer;

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

        //thread num:4,5,6 ==> 16, 25, 36 threads
        //depth: 2, 3, 4
        Benchmark.benchmarkrun();

        // test list function, if just need benchmark , comment bellow
//        CrawlerController controller = new CrawlerController(5, 3);
//
//        // list URL
//        Set<String> testUrls = Set.of(
//                "https://www.cfainstitute.org/insights/professional-learning"
//        );
//        controller.parallelCrawler.startCrawling(testUrls);
//
//
//        controller.crawlerService.pageRank();
//
//        //Out-degree
//        GraphRepository repository = new GraphRepository("bolt://localhost:7687", "neo4j", "12345678");
//        try {
//            repository.printNodeOutDegrees();
//        } finally {
//            repository.close();
//        }
    }
}


package webcrawler.controller;

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

    public CrawlerController() {
        // init the crawler service and parallel service
        this.crawlerService = new CrawlerService();
        this.parallelCrawler = new ParallelCrawler();
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

    public void database_run() {
        // 配置 Neo4J 的连接信息
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "12345678";

        // 初始化 Repository
        GraphRepository repository = new GraphRepository(uri, username, password);

        try {
            // 插入图数据（节点和边）
            insertGraphData(repository);

            // 创建 GDS 图投影
            repository.createGraphProjection();

            // 运行 PageRank 算法
            repository.runPageRank();
        } finally {
            // 关闭连接
            repository.close();
        }
    }

    private static void insertGraphData(GraphRepository repository) {
        // 定义示例节点
        List<Node> nodes = List.of(
                new Node("1", "Page A", "2024-11-29T10:00:00"),
                new Node("2", "Page B", "2024-11-29T10:05:00"),
                new Node("3", "Page C", "2024-11-29T10:10:00"),
                new Node("4", "Page D", "2024-11-29T10:15:00"),
                new Node("5", "Page E", "2024-11-29T10:20:00")
        );


        // 定义示例边
        List<Edge> edges = List.of(
                new Edge("1", "2", "RELATES_TO"),
                new Edge("2", "3", "RELATES_TO"),
                new Edge("3", "4", "RELATES_TO"),
                new Edge("4", "5", "RELATES_TO"),
                new Edge("5", "1", "RELATES_TO")
        );

        // 批量插入节点
        repository.insertNodes(nodes);

        // 批量插入边
        repository.insertEdges(edges);

        System.out.println("Nodes and edges have been inserted into the graph database.");
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

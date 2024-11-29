package webcrawler.controller;

import webcrawler.repository.GraphRepository;

public class CrawlerController {
    public static void main(String[] args) {
        // 配置 Neo4J 的连接信息
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "12345678";

        // 初始化 Repository
        GraphRepository repository = new GraphRepository(uri, username, password);

        try {
            // 初始化图数据（如果数据已经导入，可以注释此行）
            repository.initializeGraphData();

            // 创建 GDS 图投影
            repository.createGraphProjection();

            // 运行 PageRank 算法
            repository.runPageRank();
        } finally {
            // 关闭连接
            repository.close();
        }
    }
}

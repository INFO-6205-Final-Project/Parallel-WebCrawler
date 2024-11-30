package webcrawler.repository;

import org.neo4j.driver.*;

public class Neo4JConnectionTest {
    public static void main(String[] args) {
        // Neo4J 连接配置
        String uri = "bolt://localhost:7687"; // Neo4J 的 Bolt 协议地址
        String username = "neo4j";            // 用户名
        String password = "12345678";

        // 初始化 Driver
        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))) {
            // 测试连接
            System.out.println("Connecting to Neo4J...");

            try (Session session = driver.session()) {
                String greeting = session.writeTransaction(tx -> {
                    var result = tx.run("RETURN 'Hello, Neo4J!' AS message");
                    return result.single().get("message").asString();
                });

                // 打印结果
                System.out.println("Greeting from Neo4J: " + greeting);
            }
        } catch (Exception e) {
            System.err.println("Error connecting to Neo4J: " + e.getMessage());
        }
    }
}
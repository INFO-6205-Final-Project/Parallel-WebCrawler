package webcrawler.repository;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import webcrawler.model.Edge;
import webcrawler.model.Node;


public class GraphRepository {
    private final Driver driver;

    public GraphRepository(String uri, String username, String password) {
        this.driver = (Driver) GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    // 关闭连接
    public void close() {
        if (driver != null) {
            driver.close(); // 调用 Driver 的 close 方法
        }
    }

    // 添加节点
    public void addNode(Node node) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "MERGE (n:Page {id: $id}) " +
                            "SET n.title = $title, n.crawlTime = $crawlTime",
                    Values.parameters(
                            "id", node.getId())
            ));
        }
    }

    // 添加关系
    public void addEdge(Edge edge) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (a:Page {id: $from}), (b:Page {id: $to}) " +
                            "MERGE (a)-[r:LINKS_TO]->(b) " +
                            "SET r.type = $type",
                    Values.parameters(
                            "from", edge.getFrom(),
                            "to", edge.getTo(),
                            "type", edge.getRelationshipType()
                    )
            ));
        }
    }

    // 查询节点
    public void getNode(String nodeId) {
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH (n:Page {id: $id}) RETURN n",
                    Values.parameters("id", nodeId)
            );

            result.list().forEach(record -> {
                System.out.println("Node: " + record.get("n").asMap());
            });
        }
    }

    // 查询关系
    public void getEdges(String fromNode) {
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH (a:Page {id: $from})-[r:LINKS_TO]->(b:Page) " +
                            "RETURN a, r, b",
                    Values.parameters("from", fromNode)
            );

            result.list().forEach(record -> {
                System.out.println("From: " + record.get("a").asMap() +
                        ", To: " + record.get("b").asMap() +
                        ", Relationship: " + record.get("r").asMap());
            });
        }
    }


    // 初始化图数据（可选：如果数据已经导入，可忽略此方法）
    public void initializeGraphData() {
        try (Session session = driver.session()) {
            String query = """
            UNWIND [
                {id: '1', name: 'Page A'},
                {id: '2', name: 'Page B'},
                {id: '3', name: 'Page C'},
                {id: '4', name: 'Page D'},
                {id: '5', name: 'Page E'}
            ] AS page
            CREATE (:Page {id: page.id, name: page.name})
            """;
            session.writeTransaction(tx -> {
                tx.run(query);
                return null;
            });
            System.out.println("Graph data initialized.");
        }
    }


    public void createGraphProjection() {
        try (Session session = driver.session()) {
            // 删除同名图（如果存在）
            String dropQuery = """
            CALL gds.graph.exists('pageGraph') YIELD exists
            WITH exists
            WHERE exists
            CALL gds.graph.drop('pageGraph') YIELD graphName
            RETURN graphName
        """;
            session.writeTransaction(tx -> {
                tx.run(dropQuery);
                return null;
            });

            // 创建新的图投影
            String createQuery = """
            CALL gds.graph.project(
                'pageGraph', 
                'Page', 
                {
                    RELATES_TO: {
                        type: 'RELATES_TO',
                        orientation: 'NATURAL'
                    }
                }
            )
        """;
            session.writeTransaction(tx -> {
                tx.run(createQuery);
                return null;
            });

            System.out.println("Graph projection created in GDS.");
        } catch (Exception e) {
            System.err.println("Error while creating graph projection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 运行 PageRank 并输出结果
    public void runPageRank() {
        try (Session session = driver.session()) {
            String query = """
                CALL gds.pageRank.stream('pageGraph', {
                    dampingFactor: 0.85,
                    maxIterations: 20
                })
                YIELD nodeId, score
                RETURN gds.util.asNode(nodeId).id AS pageId, score
                ORDER BY score DESC
                """;

            var result = session.run(query);
            System.out.println("PageRank Results:");
            while (result.hasNext()) {
                var record = result.next();
                System.out.println("Page " + record.get("pageId").asString() +
                        " has score: " + record.get("score").asDouble());
            }
        }
    }
}


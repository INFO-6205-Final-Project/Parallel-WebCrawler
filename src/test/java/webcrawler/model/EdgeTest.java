package webcrawler.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

    @Test
    public void testConstructor() {
        // 使用构造函数初始化 Edge 对象
        String from = "Node1";
        String to = "Node2";
        String relationshipType = "LINKS_TO";

        Edge edge = new Edge(from, to, relationshipType);

        // 验证构造函数正确赋值
        assertEquals(from, edge.getFrom(), "起点节点 ID 应该匹配");
        assertEquals(to, edge.getTo(), "终点节点 ID 应该匹配");
        assertEquals(relationshipType, edge.getRelationshipType(), "关系类型应该匹配");
    }

    @Test
    public void testGetters() {
        // 创建 Edge 对象
        Edge edge = new Edge("Node1", "Node2", "LINKS_TO");

        // 验证 Getter 方法
        assertEquals("Node1", edge.getFrom(), "Getter 方法应返回正确的起点节点 ID");
        assertEquals("Node2", edge.getTo(), "Getter 方法应返回正确的终点节点 ID");
        assertEquals("LINKS_TO", edge.getRelationshipType(), "Getter 方法应返回正确的关系类型");
    }

    @Test
    public void testSetters() {
        // 创建 Edge 对象
        Edge edge = new Edge("Node1", "Node2", "LINKS_TO");

        // 设置新值
        edge.setFrom("NewNode1");
        edge.setTo("NewNode2");
        edge.setRelationshipType("REFERENCES");

        // 验证 Setter 方法是否正确设置值
        assertEquals("NewNode1", edge.getFrom(), "Setter 方法应正确设置起点节点 ID");
        assertEquals("NewNode2", edge.getTo(), "Setter 方法应正确设置终点节点 ID");
        assertEquals("REFERENCES", edge.getRelationshipType(), "Setter 方法应正确设置关系类型");
    }
}
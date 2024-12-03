package webcrawler.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {

    @Test
    public void testConstructor() {
        // 定义测试数据
        String id = "Node1";
        String title = "Sample Title";
        String crawlTime = "2024-12-01T10:00:00Z";

        // 使用构造函数初始化 Node 对象
        Node node = new Node(id, title, crawlTime);

        // 验证构造函数正确赋值
        assertEquals(id, node.getId(), "ID 应该匹配");
        assertEquals(title, node.getTitle(), "Title 应该匹配");
        assertEquals(crawlTime, node.getCrawlTime(), "CrawlTime 应该匹配");
    }

    @Test
    public void testGetters() {
        // 定义测试数据
        String id = "Node2";
        String title = "Another Title";
        String crawlTime = "2024-12-01T12:00:00Z";

        // 创建 Node 对象
        Node node = new Node(id, title, crawlTime);

        // 测试 Getter 方法
        assertEquals(id, node.getId(), "ID 应该匹配");
        assertEquals(title, node.getTitle(), "Title 应该匹配");
        assertEquals(crawlTime, node.getCrawlTime(), "CrawlTime 应该匹配");
    }
}
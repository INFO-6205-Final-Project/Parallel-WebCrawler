package webcrawler.model;

public class Node {
    private String id;      // 唯一标识（URL 或节点 ID）


    public Node(String id, String title, String crawlTime) {
        this.id = id;

    }

    // Getter 和 Setter 方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

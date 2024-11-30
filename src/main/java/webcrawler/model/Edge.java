
package webcrawler.model;

public class Edge {

    private String from;  // 起点节点的 ID
    private String to;    // 终点节点的 ID
    private String relationshipType; // 关系类型

    public Edge(String from, String to, String relationshipType) {
        this.from = from;
        this.to = to;
        this.relationshipType = relationshipType;
    }

    // Getter 和 Setter 方法
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}

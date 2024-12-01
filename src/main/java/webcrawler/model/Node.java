package webcrawler.model;

public class Node {
    private String id;
    private String title; // 新增字段
    private String crawlTime; // 新增字段

    public Node(String id, String title, String crawlTime) {
        this.id = id;
        this.title = title;
        this.crawlTime = crawlTime;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCrawlTime() {
        return crawlTime;
    }
}

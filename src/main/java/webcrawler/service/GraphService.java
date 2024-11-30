
package webcrawler.service;


import webcrawler.model.Node;
import webcrawler.model.Edge;
import webcrawler.repository.GraphRepository;

public class GraphService {
    private final GraphRepository graphRepository;

    public GraphService(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    // 添加网页节点
    public void savePageNode(String id, String title, String crawlTime) {
        Node node = new Node(id, title, "2024-11-29T10:00:00");
        graphRepository.addNode(node);
    }

    // 添加链接关系
    public void saveLink(String from, String to, String type) {
        Edge edge = new Edge(from, to, type);
        graphRepository.addEdge(edge);
    }

    // 查询网页信息
    public void getPageInfo(String nodeId) {
        graphRepository.getNode(nodeId);
    }

    // 查询链接信息
    public void getLinks(String fromNode) {
        graphRepository.getEdges(fromNode);
    }

}

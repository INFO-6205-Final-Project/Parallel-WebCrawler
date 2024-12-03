package webcrawler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import webcrawler.model.Edge;
import webcrawler.model.Node;
import webcrawler.repository.GraphRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GraphServiceTest {

    private GraphService graphService;
    private GraphRepository mockGraphRepository;

    @BeforeEach
    public void setUp() throws Exception {
        mockGraphRepository = mock(GraphRepository.class);

        // Use reflection to inject the mock repository
        graphService = new GraphService();
        var field = GraphService.class.getDeclaredField("graphRepository");
        field.setAccessible(true);
        field.set(graphService, mockGraphRepository);
    }


    @Test
    public void testSavePageNode() {
        // Test data
        String nodeId = "https://example.com";
        String title = "Example Title";
        String crawlTime = "2024-12-01T12:00:00";

        // Call the method
        graphService.savePageNode(nodeId, title, crawlTime);

        // Verify the repository interaction
        verify(mockGraphRepository, times(1)).addNode(any(Node.class));
    }

    @Test
    public void testSaveLink() {
        // Test data
        String fromNode = "https://example.com";
        String toNode = "https://example.com/about";
        String type = "RELATES_TO";

        // Call the method
        graphService.saveLink(fromNode, toNode, type);

        // Verify the repository interaction
        verify(mockGraphRepository, times(1)).addEdge(any(Edge.class));
    }

    @Test
    public void testGetPageInfo() {
        // Test data
        String nodeId = "https://example.com";

        // Call the method
        graphService.getPageInfo(nodeId);

        // Verify the repository interaction
        verify(mockGraphRepository, times(1)).getNode(nodeId);
    }

    @Test
    public void testGetLinks() {
        // Test data
        String fromNode = "https://example.com";

        // Call the method
        graphService.getLinks(fromNode);

        // Verify the repository interaction
        verify(mockGraphRepository, times(1)).getEdges(fromNode);
    }
}
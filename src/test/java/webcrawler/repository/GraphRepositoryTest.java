package webcrawler.repository;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.neo4j.driver.*;
import webcrawler.model.Edge;
import webcrawler.model.Node;

import java.util.List;

import static org.mockito.Mockito.*;

class GraphRepositoryTest {

    @Mock
    private Driver mockDriver;

    @Mock
    private Session mockSession;

    @Mock
    private Transaction mockTransaction;

    private GraphRepository graphRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        graphRepository = new GraphRepository(mockDriver);
        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.writeTransaction(any())).thenAnswer(invocation -> {
            TransactionWork<?> work = invocation.getArgument(0);
            return work.execute(mockTransaction);
        });
    }

    @AfterEach
    void tearDown() {
        graphRepository.close();
    }

    @Test
    void testAddNode() {
        Node node = new Node("1", "Test Page", "2023-12-01");

        graphRepository.addNode(node);

        verify(mockSession).writeTransaction(any());
        verify(mockTransaction).run(
                eq("MERGE (n:Page {id: $id}) SET n.title = $title, n.crawlTime = $crawlTime"),
                eq(Values.parameters("id", "1", "title", "Test Page", "crawlTime", "2023-12-01"))
        );
    }

    @Test
    void testAddEdge() {
        Edge edge = new Edge("1", "2", "RELATES_TO");

        graphRepository.addEdge(edge);

        verify(mockSession).writeTransaction(any());
        verify(mockTransaction).run(
                eq("MERGE (a:Page {id: $from}) MERGE (b:Page {id: $to}) MERGE (a)-[r:RELATES_TO]->(b) SET r.type = $type"),
                eq(Values.parameters("from", "1", "to", "2", "type", "RELATES_TO"))
        );
    }

    @Test
    void testGetNode() {
        when(mockSession.run(anyString(), any())).thenReturn(mock(Result.class));

        graphRepository.getNode("1");

        verify(mockSession).run(
                eq("MATCH (n:Page {id: $id}) RETURN n"),
                eq(Values.parameters("id", "1"))
        );
    }

    @Test
    void testGetEdges() {
        when(mockSession.run(anyString(), any())).thenReturn(mock(Result.class));

        graphRepository.getEdges("1");

        verify(mockSession).run(
                eq("MATCH (a:Page {id: $from})-[r:LINKS_TO]->(b:Page) RETURN a, r, b"),
                eq(Values.parameters("from", "1"))
        );
    }

    @Test
    void testInsertNodes() {
        List<Node> nodes = List.of(
                new Node("1", "Page A", "2023-12-01"),
                new Node("2", "Page B", "2023-12-01")
        );

        graphRepository.insertNodes(nodes);

        verify(mockSession, times(2)).writeTransaction(any());
    }

    @Test
    void testInsertEdges() {
        List<Edge> edges = List.of(
                new Edge("1", "2", "RELATES_TO"),
                new Edge("2", "3", "RELATES_TO")
        );

        graphRepository.insertEdges(edges);

        verify(mockSession, times(2)).writeTransaction(any());
    }
}

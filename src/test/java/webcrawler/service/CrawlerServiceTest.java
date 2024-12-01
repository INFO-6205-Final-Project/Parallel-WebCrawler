package webcrawler.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import webcrawler.DTO.CrawlResultDTO;
import webcrawler.util.HttpUtils;

import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerServiceTest {

    private CrawlerService crawlerService;
    private GraphService mockGraphService;

    @BeforeEach
    public void setUp() {
        // Instantiate CrawlerService with mock dependencies
        crawlerService = new CrawlerService();
        mockGraphService = mock(GraphService.class);

        // Replace the internal GraphService instance with the mock
        try {
            var field = CrawlerService.class.getDeclaredField("graphService");
            field.setAccessible(true);
            field.set(crawlerService, mockGraphService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock GraphService", e);
        }
    }

    @Test
    public void testCrawlSuccess() throws Exception {
        // Mock the behavior of HttpUtils.fetchPage
        String testUrl = "https://www.example.com";
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);

        // Mock document title and links
        when(mockDocument.title()).thenReturn("Example Domain");
        when(mockDocument.select("a[href]")).thenReturn(mockElements);
        when(mockElements.iterator()).thenReturn(Set.of(mockElement).iterator());
        when(mockElement.attr("abs:href")).thenReturn("https://www.example.com/about");

        // Mock the HttpUtils.fetchPage method
        Mockito.mockStatic(HttpUtils.class).when(() -> HttpUtils.fetchPage(testUrl)).thenReturn(mockDocument);

        // Execute the crawl method
        CrawlResultDTO result = crawlerService.crawl(testUrl);

        // Validate results
        assertNotNull(result, "Crawl result should not be null");
        assertEquals("Example Domain", result.getTitle(), "Title should match the mock document");
        assertTrue(result.getExtractedUrls().contains("https://www.example.com/about"), "Extracted URLs should contain the mocked link");

        // Cleanup static mock
        Mockito.clearAllCaches();
    }

    @Test
    public void testCrawlFailure() {
        String testUrl = "invalid-url";

        // Mock the HttpUtils.fetchPage to throw an exception
        Mockito.mockStatic(HttpUtils.class).when(() -> HttpUtils.fetchPage(testUrl)).thenThrow(new IllegalArgumentException("Invalid URL"));

        // Execute the crawl method
        CrawlResultDTO result = crawlerService.crawl(testUrl);

        // Validate the result
        assertNotNull(result, "Crawl result should not be null");
        assertNotNull(result.getExtractedUrls(), "Extracted URLs should not be null");
        assertEquals(0, result.getExtractedUrls().size(), "Extracted URLs should be empty on failure");

        // Cleanup static mock
        Mockito.clearAllCaches();
    }


    @Test
    public void testStoreData() {
        // Mock the GraphService save methods
        doNothing().when(mockGraphService).savePageNode(anyString(), anyString(), anyString());
        doNothing().when(mockGraphService).saveLink(anyString(), anyString(), anyString());

        // Test data
        String fromURL = "https://www.example.com";
        String toURL = "https://www.example.com/about";
        String title = "Example Domain";
        String crawlTime = "2024-12-01 12:00:00";

        // Execute the storeData method
        crawlerService.storeData(fromURL, toURL, title, crawlTime);

        // Verify the interactions with GraphService
        verify(mockGraphService).savePageNode(fromURL, title, crawlTime);
        verify(mockGraphService).savePageNode(toURL, toURL, crawlTime);
        verify(mockGraphService).saveLink(fromURL, toURL, "RELATES_TO");
    }
}

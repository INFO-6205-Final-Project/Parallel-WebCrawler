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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerServiceTest {

    private CrawlerService crawlerService;
    private GraphService mockGraphService;

    @BeforeEach
    public void setUp() {
        // Initialize CrawlerService and inject mocked GraphService
        crawlerService = new CrawlerService();
        mockGraphService = mock(GraphService.class);

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
        String testUrl = "https://www.example.com";

        // Mocking Document and its behavior
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);

        when(mockDocument.title()).thenReturn("Example Domain");
        when(mockDocument.select("a[href]")).thenReturn(mockElements);
        when(mockElements.iterator()).thenReturn(Set.of(mockElement).iterator());
        when(mockElement.attr("abs:href")).thenReturn("https://www.example.com/about");

        // Mock static HttpUtils.fetchPage
        try (var mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.fetchPage(testUrl)).thenReturn(mockDocument);

            // Call the method under test
            CrawlResultDTO result = crawlerService.crawl(testUrl);

            // Assertions
            assertNotNull(result, "Crawl result should not be null");
            assertEquals("Example Domain", result.getTitle(), "Title should match the mock document");
            assertTrue(result.getExtractedUrls().contains("https://www.example.com/about"),
                    "Extracted URLs should contain the mocked link");
        }
    }

    @Test
    public void testCrawlFailure() {
        String testUrl = "invalid-url";

        // Mock static HttpUtils.fetchPage to throw an exception
        try (var mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.fetchPage(testUrl))
                    .thenThrow(new IllegalArgumentException("Invalid URL"));

            // Call the method under test
            CrawlResultDTO result = crawlerService.crawl(testUrl);

            // Assertions
            assertNotNull(result, "Crawl result should not be null");
            assertNotNull(result.getExtractedUrls(), "Extracted URLs should not be null");
            assertEquals(0, result.getExtractedUrls().size(), "Extracted URLs should be empty on failure");
        }
    }

    @Test
    public void testStoreData() {
        // Mock GraphService methods
        doNothing().when(mockGraphService).savePageNode(anyString(), anyString(), anyString());
        doNothing().when(mockGraphService).saveLink(anyString(), anyString(), anyString());

        // Test data
        String fromURL = "https://www.example.com";
        String toURL = "https://www.example.com/about";
        String title = "Example Domain";
        String crawlTime = "2024-12-01 12:00:00";

        // Call the method under test
        crawlerService.storeData(fromURL, toURL, title, crawlTime);

        // Verify interactions with GraphService
        verify(mockGraphService, times(1)).savePageNode(fromURL, title, crawlTime);
        verify(mockGraphService, times(1)).savePageNode(toURL, toURL, crawlTime);
        verify(mockGraphService, times(1)).saveLink(fromURL, toURL, "RELATES_TO");
    }
}

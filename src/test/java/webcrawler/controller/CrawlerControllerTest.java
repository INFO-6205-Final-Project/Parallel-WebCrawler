package webcrawler.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import webcrawler.DTO.CrawlResultDTO;
import webcrawler.parallel.ParallelCrawler;
import webcrawler.service.CrawlerService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CrawlerControllerTest {

    private CrawlerService mockCrawlerService;
    private ParallelCrawler mockParallelCrawler;
    private CrawlerController crawlerController;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        mockCrawlerService = mock(CrawlerService.class);
        mockParallelCrawler = mock(ParallelCrawler.class);

        // Inject mocks into CrawlerController
        crawlerController = new CrawlerController(mockCrawlerService, mockParallelCrawler);
    }

    @Test
    void testStartCrawling() {
        // Arrange
        List<String> startUrls = List.of("https://www.example.com", "https://www.anotherexample.com");

        // Create mock CrawlResultDTO objects
        CrawlResultDTO result1 = new CrawlResultDTO(
                "https://www.example.com",
                "Example Title",
                "2024-12-02T10:00:00",
                new ConcurrentSkipListSet<>(Set.of("https://www.example.com/about", "https://www.example.com/contact"))
        );
        CrawlResultDTO result2 = new CrawlResultDTO(
                "https://www.anotherexample.com",
                "Another Title",
                "2024-12-02T10:05:00",
                new ConcurrentSkipListSet<>(Set.of("https://www.anotherexample.com/services"))
        );

        // Mock the crawl method
        when(mockCrawlerService.crawl("https://www.example.com")).thenReturn(result1);
        when(mockCrawlerService.crawl("https://www.anotherexample.com")).thenReturn(result2);

        // Act
        crawlerController.startCrawling(startUrls);

        // Assert crawl calls
        verify(mockCrawlerService, times(1)).crawl("https://www.example.com");
        verify(mockCrawlerService, times(1)).crawl("https://www.anotherexample.com");

        // Capture arguments for storeData calls
        ArgumentCaptor<String> fromUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> crawlTimeCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockCrawlerService, times(3)).storeData(
                fromUrlCaptor.capture(),
                toUrlCaptor.capture(),
                titleCaptor.capture(),
                crawlTimeCaptor.capture()
        );

        // Validate the captured arguments
        List<String> fromUrls = fromUrlCaptor.getAllValues();
        List<String> toUrls = toUrlCaptor.getAllValues();
        List<String> titles = titleCaptor.getAllValues();
        List<String> crawlTimes = crawlTimeCaptor.getAllValues();

        assertTrue(fromUrls.contains("https://www.example.com"));
        assertTrue(toUrls.contains("https://www.example.com/about"));
        assertTrue(toUrls.contains("https://www.example.com/contact"));
        assertTrue(titles.contains("Example Title"));
        assertTrue(crawlTimes.contains("2024-12-02T10:00:00"));

        assertTrue(fromUrls.contains("https://www.anotherexample.com"));
        assertTrue(toUrls.contains("https://www.anotherexample.com/services"));
        assertTrue(titles.contains("Another Title"));
        assertTrue(crawlTimes.contains("2024-12-02T10:05:00"));
    }

    @Test
    void testParallelCrawlerIntegration() {
        // Arrange
        Set<String> startUrls = Set.of("https://www.example.com");

        // Act
        crawlerController.getParallelCrawler().startCrawling(startUrls);

        // Assert
        verify(mockParallelCrawler, times(1)).startCrawling(startUrls);
    }
}

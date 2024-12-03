package webcrawler.DTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;

class CrawlResultDTOTest {

    private CrawlResultDTO crawlResult;

    @BeforeEach
    void setUp() {
        crawlResult = new CrawlResultDTO();
    }

    @Test
    void testSetAllElements() {
        // Arrange
        String url = "https://www.example.com";
        String title = "Example Title";
        String crawlTime = "2024-12-02T10:00:00";
        ConcurrentSkipListSet<String> extractedUrls = new ConcurrentSkipListSet<>();
        extractedUrls.add("https://www.example.com/about");
        extractedUrls.add("https://www.example.com/contact");

        // Act
        crawlResult.setAllElements(url, title, crawlTime, extractedUrls);

        // Assert
        assertEquals(url, crawlResult.getUrl());
        assertEquals(title, crawlResult.getTitle());
        assertEquals(crawlTime, crawlResult.getCrawlTime());
        assertEquals(extractedUrls, crawlResult.getExtractedUrls());
    }

    @Test
    void testAddURL() {
        // Arrange
        ConcurrentSkipListSet<String> extractedUrls = new ConcurrentSkipListSet<>();
        extractedUrls.add("https://www.example.com/about");
        crawlResult.setExtractedUrls(extractedUrls);

        // Act
        crawlResult.addURL("https://www.example.com/contact");

        // Assert
        assertEquals(2, crawlResult.getExtractedUrls().size());
        assertTrue(crawlResult.getExtractedUrls().contains("https://www.example.com/about"));
        assertTrue(crawlResult.getExtractedUrls().contains("https://www.example.com/contact"));
    }

    @Test
    void testToString() {
        // Arrange
        String url = "https://www.example.com";
        String title = "Example Title";
        String crawlTime = "2024-12-02T10:00:00";
        ConcurrentSkipListSet<String> extractedUrls = new ConcurrentSkipListSet<>();
        extractedUrls.add("https://www.example.com/about");
        extractedUrls.add("https://www.example.com/contact");
        crawlResult.setAllElements(url, title, crawlTime, extractedUrls);

        // Act
        String resultString = crawlResult.toString();

        // Assert
        assertTrue(resultString.contains(url));
        assertTrue(resultString.contains(title));
        assertTrue(resultString.contains(crawlTime));
        assertTrue(resultString.contains("https://www.example.com/about"));
        assertTrue(resultString.contains("https://www.example.com/contact"));
    }
}

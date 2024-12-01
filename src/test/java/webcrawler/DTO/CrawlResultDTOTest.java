package webcrawler.DTO;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CrawlResultDTOTest {

    @Test
    public void testDefaultConstructor() {
        // Test default constructor
        CrawlResultDTO crawlResult = new CrawlResultDTO();
        assertNotNull(crawlResult, "CrawlResultDTO instance should be created.");
    }

    @Test
    public void testParameterizedConstructor() {
        // Test parameterized constructor
        String url = "https://example.com";
        String title = "Example";
        String crawlTime = "2024-12-01T10:00:00Z";
        Set<String> extractedUrls = new HashSet<>();
        extractedUrls.add("https://example.com/page1");

        CrawlResultDTO crawlResult = new CrawlResultDTO(url, title, crawlTime, extractedUrls);

        assertEquals(url, crawlResult.getUrl(), "URL should match.");
        assertEquals(title, crawlResult.getTitle(), "Title should match.");
        assertEquals(crawlTime, crawlResult.getCrawlTime(), "Crawl time should match.");
        assertEquals(extractedUrls, crawlResult.getExtractedUrls(), "Extracted URLs should match.");
    }

    @Test
    public void testSettersAndGetters() {
        // Test individual setters and getters
        CrawlResultDTO crawlResult = new CrawlResultDTO();

        String url = "https://example.com";
        String title = "Example";
        String crawlTime = "2024-12-01T10:00:00Z";
        Set<String> extractedUrls = new HashSet<>();
        extractedUrls.add("https://example.com/page1");

        crawlResult.setUrl(url);
        crawlResult.setTitle(title);
        crawlResult.setCrawlTime(crawlTime);
        crawlResult.setExtractedUrls(extractedUrls);

        assertEquals(url, crawlResult.getUrl(), "URL should match.");
        assertEquals(title, crawlResult.getTitle(), "Title should match.");
        assertEquals(crawlTime, crawlResult.getCrawlTime(), "Crawl time should match.");
        assertEquals(extractedUrls, crawlResult.getExtractedUrls(), "Extracted URLs should match.");
    }

    @Test
    public void testSetAllElements() {
        // Test setAllElements method
        CrawlResultDTO crawlResult = new CrawlResultDTO();

        String url = "https://example.com";
        String title = "Example";
        String crawlTime = "2024-12-01T10:00:00Z";
        Set<String> extractedUrls = new HashSet<>();
        extractedUrls.add("https://example.com/page1");

        crawlResult.setAllElements(url, title, crawlTime, extractedUrls);

        assertEquals(url, crawlResult.getUrl(), "URL should match.");
        assertEquals(title, crawlResult.getTitle(), "Title should match.");
        assertEquals(crawlTime, crawlResult.getCrawlTime(), "Crawl time should match.");
        assertEquals(extractedUrls, crawlResult.getExtractedUrls(), "Extracted URLs should match.");
    }

    @Test
    public void testToString() {
        // Test toString method
        String url = "https://example.com";
        String title = "Example";
        String crawlTime = "2024-12-01T10:00:00Z";
        Set<String> extractedUrls = new HashSet<>();
        extractedUrls.add("https://example.com/page1");

        CrawlResultDTO crawlResult = new CrawlResultDTO(url, title, crawlTime, extractedUrls);

        String expected = "CrawlResultDTO{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", crawlTime='" + crawlTime + '\'' +
                ", extractedUrls=" + extractedUrls +
                '}';

        assertEquals(expected, crawlResult.toString(), "toString output should match.");
    }
}

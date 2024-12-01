package webcrawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class HttpUtilsTest {

    @Test
    public void testFetchPageSuccess() throws Exception {
        String testUrl = "https://www.example.com";
        Document document = HttpUtils.fetchPage(testUrl);
        assertNotNull(document, "Document should not be null");
        assertEquals("Example Domain", document.title(), "The page title should match");
    }

    @Test
    public void testFetchPageWithMock() throws Exception {
        String testUrl = "https://mockurl.com";
        Document mockDocument = Jsoup.parse("<html><head><title>Mock Title</title></head><body></body></html>");

        Connection mockConnection = mock(Connection.class);
        when(mockConnection.get()).thenReturn(mockDocument);

        try (var mockStaticJsoup = Mockito.mockStatic(Jsoup.class)) {
            mockStaticJsoup.when(() -> Jsoup.connect(testUrl)).thenReturn(mockConnection);
            Document document = HttpUtils.fetchPage(testUrl);
            assertNotNull(document, "Document should not be null");
            assertEquals("Mock Title", document.title(), "The mock page title should match");
        }
    }

    @Test
    public void testFetchPageFailure() {
        String invalidUrl = "invalid-url";

        Exception exception = null;
        try {
            HttpUtils.fetchPage(invalidUrl);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception, "An exception should be thrown for an invalid URL");
        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException, "Exception should be of type IllegalArgumentException");
        assertTrue(exception.getMessage().contains("The supplied URL"),
                "Exception message should indicate the URL is malformed");
    }
}

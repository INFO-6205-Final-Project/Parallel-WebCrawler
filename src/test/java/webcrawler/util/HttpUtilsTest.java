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
        // Arrange
        String testUrl = "https://www.example.com";

        // Act
        Document document = HttpUtils.fetchPage(testUrl);

        // Assert
        assertNotNull(document, "Document should not be null");
        assertEquals("Example Domain", document.title(), "The page title should match");
    }

    @Test
    public void testFetchPageWithMock() throws Exception {
        // Arrange
        String testUrl = "https://mockurl.com";
        Document mockDocument = Jsoup.parse("<html><head><title>Mock Title</title></head><body></body></html>");

        // Mock Jsoup behavior
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.get()).thenReturn(mockDocument);

        try (var mockStaticJsoup = Mockito.mockStatic(Jsoup.class)) {
            mockStaticJsoup.when(() -> Jsoup.connect(testUrl)).thenReturn(mockConnection);

            // Act
            Document document = HttpUtils.fetchPage(testUrl);

            // Assert
            assertNotNull(document, "Document should not be null");
            assertEquals("Mock Title", document.title(), "The mock page title should match");
        }
    }

    @Test
    public void testFetchPageFailure() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> HttpUtils.fetchPage(invalidUrl),
                "Expected exception for invalid URL");

        // Log the exception message for debugging
        System.out.println("Actual exception message: " + exception.getMessage());

        // Verify exception message
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
                exception.getMessage().toLowerCase().contains("malformed") ||
                        exception.getMessage().toLowerCase().contains("make sure it is an absolute url"),
                "Exception message should indicate a malformed URL or connection failure"
        );


    }


}

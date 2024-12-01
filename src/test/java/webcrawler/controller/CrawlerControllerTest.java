package webcrawler.controller;

import webcrawler.DTO.CrawlResultDTO;
import webcrawler.parallel.ParallelCrawler;
import webcrawler.service.CrawlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrawlerControllerTest {

    private CrawlerService mockCrawlerService;
    private ParallelCrawler mockParallelCrawler;
    private CrawlerController crawlerController;

    @BeforeEach
    void setUp() {
        // 创建 Mock 对象
        mockCrawlerService = mock(CrawlerService.class);
        mockParallelCrawler = mock(ParallelCrawler.class);

        // 创建 CrawlerController 实例，注入 Mock 对象
        crawlerController = new CrawlerController(mockCrawlerService, mockParallelCrawler);
    }

    @Test
    void testStartCrawling() {
        // 定义测试输入
        List<String> startUrls = List.of("https://www.example.com", "https://www.anotherexample.com");

        // 定义 Mock 返回值
        CrawlResultDTO crawlResult1 = new CrawlResultDTO("https://www.example.com", "Example Title", "2024-11-29T10:00:00", Set.of("https://www.example.com/about", "https://www.example.com/contact"));
        CrawlResultDTO crawlResult2 = new CrawlResultDTO("https://www.anotherexample.com", "Another Title", "2024-11-29T10:05:00", Set.of("https://www.anotherexample.com/services"));

        // 配置 Mock 行为
        when(mockCrawlerService.crawl("https://www.example.com")).thenReturn(crawlResult1);
        when(mockCrawlerService.crawl("https://www.anotherexample.com")).thenReturn(crawlResult2);

        // 调用被测试的方法
        crawlerController.startCrawling(startUrls);

        // 验证 crawl 方法被正确调用
        verify(mockCrawlerService, times(1)).crawl("https://www.example.com");
        verify(mockCrawlerService, times(1)).crawl("https://www.anotherexample.com");

        // 捕获 storeData 方法的调用参数
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> suburlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> crawlTimeCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockCrawlerService, times(3)).storeData(urlCaptor.capture(), suburlCaptor.capture(), titleCaptor.capture(), crawlTimeCaptor.capture());

        List<String> capturedUrls = urlCaptor.getAllValues();
        List<String> capturedSuburls = suburlCaptor.getAllValues();
        List<String> capturedTitles = titleCaptor.getAllValues();
        List<String> capturedCrawlTimes = crawlTimeCaptor.getAllValues();

        // 验证 storeData 调用次数和参数
        assertEquals(3, capturedUrls.size());

        // 验证第一个 URL 的 storeData 调用
        assertTrue(capturedUrls.contains("https://www.example.com"));
        assertTrue(capturedSuburls.contains("https://www.example.com/about"));
        assertTrue(capturedSuburls.contains("https://www.example.com/contact"));
        assertTrue(capturedTitles.contains("Example Title"));
        assertTrue(capturedCrawlTimes.contains("2024-11-29T10:00:00"));

        // 验证第二个 URL 的 storeData 调用
        assertTrue(capturedUrls.contains("https://www.anotherexample.com"));
        assertTrue(capturedSuburls.contains("https://www.anotherexample.com/services"));
        assertTrue(capturedTitles.contains("Another Title"));
        assertTrue(capturedCrawlTimes.contains("2024-11-29T10:05:00"));
    }
}

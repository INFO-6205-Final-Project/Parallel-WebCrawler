package webcrawler.service;

import webcrawler.DTO.CrawlResultDTO;
import webcrawler.util.HttpUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class CrawlerService {

    private final Set<String> visitedUrls = new HashSet<>();
    private final GraphService graphService = new GraphService();

    /**
     * Just get URL content
     * @param url  URL
     * @return Datatype for one level of url(url and sub-urls)
     */
    public CrawlResultDTO crawl(String url) {
        CrawlResultDTO data = new CrawlResultDTO();
        Set<String> extractedUrls = new HashSet<>(); // Always initialize

        try {
            // Fetch HTML content
            Document document = HttpUtils.fetchPage(url);

            // Extract title and crawl time
            String title = document.title();
            String crawlTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Extract links
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String absoluteUrl = link.attr("abs:href");
                if (isValidUrl(absoluteUrl) && visitedUrls.add(absoluteUrl)) {
                    extractedUrls.add(absoluteUrl);
                }
            }

            data.setAllElements(url, title, crawlTime, extractedUrls);

        } catch (Exception e) {
            System.err.println("Failed to crawl URL: " + url + ", Error: " + e.getMessage());
            // Initialize fields even in failure cases
            data.setAllElements(url, null, null, extractedUrls);
        }

        return data;
    }

    /**
     * Check the validation of the URL
     * @param url URL
     * @return if valid
     */
    private boolean isValidUrl(String url) {
        return url.startsWith("http");
    }


    /**
     * store URL data to Graph database
     * @param fromURL Node URL
     * @param toURL Edge URL, next node url
     * @param title web title
     * @param crawlTime time
     */
    public void storeData(String fromURL, String toURL, String title, String crawlTime) {
        try {

            graphService.savePageNode(fromURL, title, crawlTime);

            graphService.savePageNode(toURL, toURL, crawlTime);

            graphService.saveLink(fromURL, toURL, "RELATES_TO");

            System.out.println("Stored data: " + fromURL + " -> " + toURL);
        } catch (Exception e) {
            System.err.println("Failed to store data: " + fromURL + " -> " + toURL + ", Error: " + e.getMessage());
        }
    }
}

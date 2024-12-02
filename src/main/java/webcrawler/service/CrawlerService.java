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
import java.util.concurrent.ConcurrentSkipListSet;

public class CrawlerService {

    private final Set<String> visitedUrls ;
    private final GraphService graphService ;


    public CrawlerService() {
        this.visitedUrls = new HashSet<>();
        this.graphService = new GraphService();
    }

    /**
     * Just get URL content
     * @param url  URL
     * @return Datatype for one level of url(url and sub-urls)
     */
    public CrawlResultDTO crawl(String url) {

        CrawlResultDTO data = new CrawlResultDTO();
        ConcurrentSkipListSet<String> extractedUrls = new ConcurrentSkipListSet<>((url1, url2) -> {
            int lengthComparison = Integer.compare(url1.length(), url2.length());
            // If lengths are equal, use natural order to ensure consistency
            return lengthComparison != 0 ? lengthComparison : url1.compareTo(url2);
        });

        try {
            // get html content
            Document document = HttpUtils.fetchPage(url);

            // get title
            String title = document.title();

            // get time
            String crawlTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // print web info
//            System.out.println("Crawled: " + url);
//            System.out.println("Title: " + title);
//            System.out.println("Crawl Time: " + crawlTime);

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String absoluteUrl = link.attr("abs:href");

                if (isValidUrl(absoluteUrl) && visitedUrls.add(absoluteUrl)) {
                    extractedUrls.add(absoluteUrl);
                }
            }

//            System.out.println("Found links: " + extractedUrls.size());
            data.setAllElements(url, title, crawlTime, extractedUrls);

        } catch (Exception e) {
//            System.out.println("Failed to crawl URL: " + url + ", Error: " + e.getMessage());
        }

        return data;
    }


    public void pageRank() {
        graphService.runPageRank();
    }

    /**
     * Check the validation of the URL
     * @param url URL
     * @return if valid
     */
    private boolean isValidUrl(String url) {

//        // Check if the URL starts with "https://www.cfainstitute.org/"
        if (!url.contains("cfainstitute")) {
            return false;
        }

        // Check if the URL starts with "http" or "https"
        if (!url.startsWith("http")) {
            return false;
        }
        // Exclude .onion domains (dark web URLs)
        if (url.endsWith(".onion")) {
            return false;
        }
        // Ignore "my" subdomains
        if (url.startsWith("https://my.") || url.startsWith("http://my.")) {
            return false;
        }
        // Ignore query parameters or session-related links
        if (url.contains("?") || url.contains("session")) {
            return false;
        }
        // Ignore anchor tags
        if (url.contains("#")) {
            return false;
        }
        // Ignore URLs containing "Account" or "Create Account" (case-insensitive)
        String lowerCaseUrl = url.toLowerCase();
        if (lowerCaseUrl.contains("account") || lowerCaseUrl.contains("create account")) {
            return false;
        }
        // Ignore URLs ending with .pdf
        if (lowerCaseUrl.endsWith(".pdf")) {
            return false;
        }
        // Ignore URLs ending with common video file extensions
        String[] videoExtensions = {".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".webm"};
        for (String ext : videoExtensions) {
            if (lowerCaseUrl.endsWith(ext)) {
                return false;
            }
        }
        return true;
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

//            System.out.println("Stored data: " + fromURL + " -> " + toURL);
        } catch (Exception e) {
//            System.err.println("Failed to store data: " + fromURL + " -> " + toURL + ", Error: " + e.getMessage());
        }
    }
}

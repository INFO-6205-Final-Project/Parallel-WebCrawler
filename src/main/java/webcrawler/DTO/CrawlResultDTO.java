package webcrawler.DTO;

import java.util.Set;

public class CrawlResultDTO {

    private String url;
    private String title;
    private String crawlTime;
    private Set<String> extractedUrls;

    // Constructor
    public CrawlResultDTO(){}

    public CrawlResultDTO(String url, String title, String crawlTime, Set<String> extractedUrls) {
        this.url = url;
        this.title = title;
        this.crawlTime = crawlTime;
        this.extractedUrls = extractedUrls;
    }

    public void setAllElements(String url, String title, String crawlTime, Set<String> extractedUrls){
        this.url = url;
        this.title = title;
        this.crawlTime = crawlTime;
        this.extractedUrls = extractedUrls;
    }

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }

    public Set<String> getExtractedUrls() {
        return extractedUrls;
    }

    public void setExtractedUrls(Set<String> extractedUrls) {
        this.extractedUrls = extractedUrls;
    }

    @Override
    public String toString() {
        return "CrawlResultDTO{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", crawlTime='" + crawlTime + '\'' +
                ", extractedUrls=" + extractedUrls +
                '}';
    }
}

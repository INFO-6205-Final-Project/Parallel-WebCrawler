
package webcrawler.service;

import webcrawler.util.HttpUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class CrawlerService {

    private final Set<String> visitedUrls = new HashSet<>();

    /**
     * Just get URL content
     * @param url  URL
     * @return valida URL from 'root' url
     */
    public Set<String> crawl(String url) {
        Set<String> extractedUrls = new HashSet<>();

        try {
            // 获取网页内容
            Document document = HttpUtils.fetchPage(url);

            // 解析页面中所有的链接
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String absoluteUrl = link.attr("abs:href");

                // 检查是否已访问过，过滤重复链接
                if (isValidUrl(absoluteUrl) && visitedUrls.add(absoluteUrl)) {
                    extractedUrls.add(absoluteUrl);
                    // TODO: just run once for crawler test
//                    break;
                }
            }

            System.out.println("Crawled: " + url + ", Found links: " + extractedUrls.size());
        } catch (Exception e) {
            System.err.println("Failed to crawl URL: " + url + ", Error: " + e.getMessage());
        }

        return extractedUrls;
    }

    /**
     * Check the validation of the URL
     * @param url URL
     * @return if valid
     */
    private boolean isValidUrl(String url) {
        return url.startsWith("http");
    }
}

package webcrawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpUtils {
    /**
     * 获取网页内容
     * @param url 目标 URL
     * @return 网页内容的 Document 对象
     * @throws Exception 如果请求失败
     */
    public static Document fetchPage(String url) throws Exception {
        return Jsoup.connect(url).get();
    }
}
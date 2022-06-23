import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser{

    WebCrawler webCrawler;
    Parser(WebCrawler webCrawler){
        this.webCrawler = webCrawler;
    }

    /**
     * Look into the HTML of an page and searches for more links for future iterations.
     * @param url - the HTML in which the links are searched
     * @param currentDepth - current iteration
     */
    public void parsingForLinksInString(String url, int currentDepth){
        //currentURLResultWriting(rawHTMLPage, currentDepth, currentURL);
        JsoupWrapper jsoup = new JsoupWrapper();
        List<String> resultUrls = new ArrayList<>();
        try{
            resultUrls = jsoup.getLinkByTagFromURL(url, "a");
        }catch (Exception e){
            System.out.println("HTTP Connection Failed. Link was broken.");
        }

        for (String currentUrl: resultUrls) {
            synchronized (this) {
                if (!(webCrawler.alreadyVisitedURLs.contains(currentUrl) || webCrawler.queueList.get(currentDepth + 1).contains(currentUrl))) {
                   webCrawler.queueList.get(currentDepth + 1).add(currentUrl);
                }
            }
        }
    }

    /**
     * Searches for header tags in th HTML file.
     * @param url - the link to the HTML in which the links are searched
     * @return the array of headers
     */
    public List<String> parsingForHeadersInString(String url){
        JsoupWrapper jsoup = new JsoupWrapper();
        try{
            return jsoup.getElementTextByTagFromURL(url, "h1, h2, h3, h4, h5, h6, h7");
        }catch (Exception e){
            System.out.println("HTTP Connection Failed. Link Broken or connection time outed.");
            return new ArrayList<String>();
        }
    }
}

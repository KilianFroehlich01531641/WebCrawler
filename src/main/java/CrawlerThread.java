import java.net.URL;

public class CrawlerThread extends Thread {
    private String urlString;
    private int currentItteration;
    private WebCrawler webCrawler;

    /**
     * Constructor.
     * @param urlString - the string to search for
     * @param currentItteration - curent depth of iteration
     * @param webCrawler - the parent who calls this thread
     */
    public CrawlerThread(String urlString, int currentItteration, WebCrawler webCrawler) {
        this.urlString = urlString;
        this.currentItteration = currentItteration;
        this.webCrawler = webCrawler;
    }

    @Override
    public void run(){
        crawl(urlString, currentItteration);
    }

    /**
     * the method that handles the lookup and then starts to write the result into the file.
     * If the URL can't be accessed will handle the link accordingly.
     * @param urlString - the url to crwal through
     * @param currentItteration - the depth level that is currently worked on
     */
    public void crawl(String urlString, int currentItteration){
        try{
            URL url = new URL(urlString);
            String currentHTML = webCrawler.getRawHTMLFromURL(url);
            if(currentItteration+1 == webCrawler.userMaximumPageDepth || currentHTML == ""){
                webCrawler.currentURLResultWriting(currentHTML, currentItteration , urlString);
            }else{
                webCrawler.parsingForLinksInString(currentHTML, currentItteration, urlString);
            }
        }catch (Exception e){
            String currentHTML = "";
            webCrawler.currentURLResultWriting(currentHTML, currentItteration, urlString);
        } finally {
            webCrawler.latch.countDown();
        }
    }
}

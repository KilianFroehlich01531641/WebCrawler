import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

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
     * @param urlString - the url to crawl through
     * @param currentItteration - the depth level that is currently worked on
     */
    public void crawl(String urlString, int currentItteration){
        try{
            currentURLResultWriting(currentItteration, urlString);
            if(currentItteration < webCrawler.userMaximumPageDepth-1){
                webCrawler.getParser().parsingForLinksInString(urlString, currentItteration);
            }
        }catch (Exception e){
            System.out.println("Critical Error in Thread "+this.getId()+".\n" +
                    "Corrupt Link: "+ urlString +".");
        }finally {
            webCrawler.latch.countDown();
        }
    }

    /**
     * Writes the currently found link into the global depthLevelResults list.
     * Also removes link from queue and adds it to visited list.
     * @param currentDepth - current iteration
     * @param currentURL - the url of the HTML page
     */
    public void currentURLResultWriting(int currentDepth, String currentURL) {
        synchronized (webCrawler.mutex){
            //webCrawler.queueList.get(currentDepth).remove(currentURL);
            if(!(webCrawler.alreadyVisitedURLs.contains(currentURL))){
                webCrawler.alreadyVisitedURLs.add(currentURL);
            }
        }

        List<String> headers = webCrawler.getParser().parsingForHeadersInString(currentURL);
        /**
         * Working translator for the Header but activating this will decrease performance drastically as each threads needs to send several HTTP requests to translate the headers
         */
        //Translater translater = new Translater(webCrawler.source, webCrawler.target);
        //headers = translater.translateHeaders(headers);
        if(headers.toString().equals("[]")){
            synchronized (webCrawler.mutex) {
                webCrawler.depthLevelResults.set(currentDepth, webCrawler.depthLevelResults.get(currentDepth) + "<br>--> broken link to <a>" + currentURL + "</a>\n\n");
            }
        }else{
            synchronized (webCrawler.mutex) {
                webCrawler.depthLevelResults.set(currentDepth, webCrawler.depthLevelResults.get(currentDepth) + "<br>--> link to <a>" + currentURL + "</a>\n" + headers.toString() + "\n");
            }
        }
    }
}

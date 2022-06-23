import sun.awt.Mutex;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler{

    /**
     * Global variables
     */
    int userMaximumPageDepth;
    ArrayList<ArrayList> queueList;
    ArrayList<String> depthLevelResults;
    String source;
    String target;
    ArrayList<String> alreadyVisitedURLs;
    CountDownLatch latch;
    Parser parser;
    final Mutex mutex = new Mutex();

    public Parser getParser() {
        return parser;
    }


    /**
     * Constructor
     * @param userPageDepth - the number of times the crawler looks into each link from a page
     * @param userURL - the starting page
     * @param source - the language the web pages are to be expected
     * @param target - the language to translate into
     */
    public WebCrawler(ArrayList userURL, int userPageDepth, String source, String target){
        this.userMaximumPageDepth = userPageDepth;
        queueList = new ArrayList<>(userPageDepth);
        depthLevelResults = new ArrayList<>(userPageDepth);
        alreadyVisitedURLs = new ArrayList<String>();
        for (int i = 0; i < userPageDepth; i++) {
            queueList.add(new ArrayList<String>());
            depthLevelResults.add("");
        }

        try{
            queueList.get(0).addAll(userURL);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.source = source;
        this.target = target;
        this.parser = new Parser(this);
    }

    /**
     * Main method of this class. Initializes the file, crawls through the webpages and prints resulst into the file.
     * this Method will start a thread for each link. it will stop after each level and wait for threads to finish.
     * If I wouldn't do that this method may stop producing threads the list is empty (because threads my not have added new items so far)
     * and this method will then wait without starting new threads, although new items get added.
     */
    public void start(){
        FileHandler fileHandler = new FileHandler(userMaximumPageDepth);
        fileHandler.initializeFile( source, target);

        for (int i = 0; i < userMaximumPageDepth; i++) {
            latch = new CountDownLatch(queueList.get(i).size());
            for (int j = 0; j < queueList.get(i).size() ; j++) {
                System.out.println("current link: " + queueList.get(i).get(j));
                CrawlerThread thread = new CrawlerThread(queueList.get(i).get(j).toString(), i, this);
                thread.start();
            }
            try{
                latch.await();
            } catch (InterruptedException e){
                System.out.print(e);
            }
            System.out.println("going deeper: Level " + (i+1)+ " now.");
        }
        fileHandler.writeResultsToFile(depthLevelResults);
    }
}

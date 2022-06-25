import jdk.internal.jline.internal.Urls;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.*;

public class TestClass {

    private WebCrawler webCrawler;

    @Before
    public void setup(){
        ArrayList<String> links = new ArrayList<>();
        links.add("https://www.w3schools.com/tags/tag_header.asp");
        webCrawler = new WebCrawler(links,2, "en", "de");
    }

    @Test
    public void testWhole(){
        ArrayList<String> links = new ArrayList<>();
        links.add("https://www.w3schools.com/tags/tag_header.asp");

        webCrawler = new WebCrawler(links, 2, "en", "de");
        try {
            webCrawler.start();
        }finally {
            File testFile = new File("result_"+ webCrawler.userMaximumPageDepth + ".md");
            testFile.delete();
        }
    }

    @Test
    public void addSeveralLinks(){
        ArrayList<String> links = new ArrayList<>();
        links.add("https://www.w3schools.com/tags/tag_header.asp");
        links.add("https://www.w3schools.com");
        links.add("https://www.google.com");

        WebCrawler webCrawler1 = new WebCrawler(links, 1, "en", "de");
        Assert.assertEquals(webCrawler1.queueList.get(0), links);
    }

}


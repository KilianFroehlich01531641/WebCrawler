import jdk.internal.jline.internal.Urls;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;

public class TestClass {

    private WebCrawler webCrawler;

    @Before
    public void setup(){
        webCrawler = new WebCrawler(2, "https://www.w3schools.com/tags/tag_header.asp", "en", "de");
    }

    @Test
    public void testingIfURLIsReadRight() throws MalformedURLException {

        String result = webCrawler.getRawHTMLFromURL(new URL("http://www.google.at"));
        assertNotNull(result);
        assertTrue(result.contains("<html"));
    }

    @Test(expected = Exception.class)
    public void testingIfWrongURLIsHandledRight() throws Exception{
        assertEquals("", webCrawler.getRawHTMLFromURL(new URL("")));
    }

    @Test
    public void creatingFileTest(){
        try{
            //creating file first time
            assertTrue(webCrawler.createMDFile());
            //creating it second time
            assertFalse(webCrawler.createMDFile());
        }finally {
            //deleting the file so other test are not affected
            File testfile = new File("result_"+ webCrawler.userMaximumPageDepth + ".md");
            testfile.delete();
        }
    }

    @Test
    public void writeToFileTest(){
        String teststring = "Testing the method345@//##?._ .";
        webCrawler.createMDFile();
        webCrawler.writeMDFile(teststring);
        try{
            File testFile = new File("result_"+ webCrawler.userMaximumPageDepth + ".md");
            Scanner scanner = new Scanner(testFile);
            assertEquals(teststring, scanner.nextLine());
            scanner.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            File testFile = new File("result_"+ webCrawler.userMaximumPageDepth + ".md");
            testFile.delete();
        }
    }

    @Test
    public void lookingForHeaders(){
        try{
            webCrawler.parsingForHeadersInString(webCrawler.getRawHTMLFromURL(new URL("https://www.w3schools.com/tags/tag_header.asp")), 0);
            assertEquals("<h3>Example</h3>\n" +
                    "<h3>Example</h3>\n" +
                    "<h4>Exercises</h4>\n" +
                    "\n", webCrawler.depthLevelResults.get(0));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    public void lookingForHeadersInEmptyHTML(){
        try{
            webCrawler.parsingForHeadersInString("", 0);
            assertEquals("\n", webCrawler.depthLevelResults.get(0));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void resultWritingTest(){
        try {
            webCrawler.currentURLResultWriting(webCrawler.getRawHTMLFromURL(new URL("https://www.w3schools.com/tags/tag_header.asp")), 0, "https://www.w3schools.com/tags/tag_header.asp");
        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(webCrawler.depthLevelResults.get(0).contains("<br>--> link to <a>"));
    }

    @Test
    public void resultWritingTestWithEmptyHTML(){
        try {
            webCrawler.currentURLResultWriting("", 0, "https://www.w3schools.com/tags/tag_header.asp");
        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(webCrawler.depthLevelResults.get(0).contains("<br>--> broken link to <a>"));
    }

    @Test
    public void URLAddingToQueue(){
        try{
            webCrawler.parsingForLinksInString(webCrawler.getRawHTMLFromURL(new URL("https://www.w3schools.com/tags/tag_header.asp")), 0, "https://www.w3schools.com/tags/tag_header.asp");
        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(webCrawler.queueList.get(1).size() != 0);
    }

    @Test
    public void testWhole(){
        //webCrawler = new WebCrawler(2, "https://www.w3schools.com/tags/tag_header.asp");
        webCrawler = new WebCrawler(2, "https://www.w3schools.com/tags/tag_header.asp", "en", "de");
        webCrawler.start();
    }

    @Test
    public void testTranslationEnToDe(){
        String answer = webCrawler.translatingString("Hello", "de", "en");
        assertEquals("Hallo", answer);
    }

    @Test
    public void testTranslationDeToEn(){
        String answer = webCrawler.translatingString("Javadoc", "en", "de");
        assertEquals("Hi", answer);
    }

    @Test
    public void headerCuttingTest(){
        String testString = "<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\">Example text</h1>";
        String testStringResult = "<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\">Beispieltext</h1>";
        assertEquals(testStringResult, webCrawler.translateHeaders(testString));
    }

}


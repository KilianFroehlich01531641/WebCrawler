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
        webCrawler = new WebCrawler(4, "http://www.google.at");
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

}


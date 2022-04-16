import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class TestClass {

    private WebCrawler webCrawler = new WebCrawler();

    @Test
    public void testingIfURLIsReadRight() throws MalformedURLException {

        String result = webCrawler.getRawHTMLFromURL(new URL("http://www.google.at"));
        assertNotNull(result);
        assertTrue(result.contains("<html"));
    }

    @Test(expected = Exception.class)
    public void testingIfWrongURLIsHAndledRight() throws Exception{
        webCrawler.getRawHTMLFromURL(new URL(""));
    }
}


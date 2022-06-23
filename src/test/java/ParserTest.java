import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ParserTest {
    private WebCrawler webCrawler;

    @Before
    public void setup(){
        ArrayList<String> links = new ArrayList<>();
        links.add("https://www.w3schools.com/tags/tag_header.asp");
        webCrawler = new WebCrawler(links,2, "en", "de");
    }

    @Test
    public void lookingForHeaders(){
        try{
            String actual = new Parser(webCrawler).parsingForHeadersInString("https://www.w3schools.com/tags/tag_header.asp").toString();
            String expectedMessage = "[Tutorials, HTML and CSS, Data Analytics, XML Tutorials, JavaScript, Programming, Server Side, Web Building, Data Analytics, XML Tutorials, References, HTML, CSS, JavaScript, Programming, Server Side, XML, Character Sets, Exercises and Quizzes, Exercises, Quizzes, Courses, Certificates, HTML Reference, HTML Tags, HTML <header> Tag, Example, Definition and Usage, Browser Support, Global Attributes, Event Attributes, More Examples, Example, Related Pages, Default CSS Settings, COLOR PICKER, CODE GAME, Report Error, Thank You For Helping Us!, Top Tutorials, Top References, Top Examples, Web Courses]";
            assertEquals(actual, expectedMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void lookingForHeadersInEmptyHTML(){
        try{
            String actual = new Parser(webCrawler).parsingForHeadersInString("").toString();
            assertEquals("[]", actual);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void lookingForHeadersInFaultHTML(){
        try{
            String actual = new Parser(webCrawler).parsingForHeadersInString("https:www.at").toString();
            assertEquals("[]", actual);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

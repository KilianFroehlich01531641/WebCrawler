import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

public class TranslatorTest {

    private Translater translater;

    @Before
    public void setup(){
        translater = new Translater("de", "en");
    }

    @Test
    public void testTranslationEnToDe(){
        String answer = translater.translatingString("Hello");
        assertEquals("Hallo", answer);
    }

    @Test
    public void testTranslationDeToEn(){
        translater.targetLanguage = "de";
        translater.sourceLanguage = "en";
        String answer = translater.translatingString("Hallo");
        assertEquals("Hi", answer);
    }
}

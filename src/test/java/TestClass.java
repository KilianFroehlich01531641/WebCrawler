import org.junit.Test;

import static org.junit.Assert.*;

public class TestClass {

    @Test
    public void tester(){
        WebCralwer webCralwer = new WebCralwer();
        assertTrue(webCralwer.test());
    }
}


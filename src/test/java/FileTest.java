import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class FileTest {
    FileHandler fileHandler = new FileHandler(1);

    @Test
    public void creatingFileTest(){
        try{
            //creating file first time
            assertTrue(fileHandler.createMDFile());
            //creating it second time
            assertFalse(fileHandler.createMDFile());
        }finally {
            //deleting the file so other test are not affected
            File testfile = new File("result_"+ fileHandler.userMaximumPageDepth + ".md");
            testfile.delete();
        }
    }

    @Test
    public void writeToFileTest(){
        String teststring = "Testing the method345@//##?._ .";
        fileHandler.createMDFile();
        fileHandler.writeMDFile(teststring);
        try{
            File testFile = new File("result_"+ fileHandler.userMaximumPageDepth + ".md");
            Scanner scanner = new Scanner(testFile);
            assertEquals(teststring, scanner.nextLine());
            scanner.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            File testFile = new File("result_"+ fileHandler.userMaximumPageDepth + ".md");
            testFile.delete();
        }
    }

}

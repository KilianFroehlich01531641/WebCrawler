import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {
    int userMaximumPageDepth;

    FileHandler(int userMaximumPageDepth){
        this.userMaximumPageDepth = userMaximumPageDepth;
    }

    /**
     * creates a MD file and writes header information into it.
     */
    public void initializeFile(String sourceLanguage, String targetLanguage){
        createMDFile();
        writeMDFile("<br>depth: "+ userMaximumPageDepth +"\n" +
                "<br>source language: "+ sourceLanguage +"\n" +
                "<br>target language: "+ targetLanguage +"\n" +
                "<br>summary: \n\n");
    }

    /**
     * Takes the gloval val depthLevelResults and writes the content into the MD file.
     */
    public void writeResultsToFile( ArrayList<String> depthLevelResults){
        int depthLevelCounter = 1;
        for (String result: depthLevelResults) {
            writeMDFile("---------Depth Level: " + depthLevelCounter + "--------\n");
            writeMDFile(result);
            depthLevelCounter++;
        }
    }

    /**
     * Creates a File.
     * @return true if the file is created; false if it already exists or an error occurred.
     */
    public boolean createMDFile(){
        File resultFile = new File("result_"+ userMaximumPageDepth + ".md");
        try{
            if(resultFile.createNewFile()){
                System.out.println("File Created.");
                return true;
            }else {
                System.out.println("File exists already.");
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Writes a string into a file.
     * @param input - the content to write into the file.
     * @return - true if success; false if an error occurred.
     */
    public boolean writeMDFile(String input){
        try{
            FileWriter resultFile = new FileWriter("result_"+ userMaximumPageDepth + ".md", true);
            resultFile.write(input);
            resultFile.close();
            return true;
        }catch (IOException e){
            System.out.println("Could not write to file.");
            return false;
        }
    }
}

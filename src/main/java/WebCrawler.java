import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private URL userURL;
    private int userPageDepth;


    public String getRawHTMLFromURL(URL userURL){
        String rawHTMLPage = "";
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(userURL.openStream()));
            String currentLineInHMTL = input.readLine();
            do{
                rawHTMLPage += (currentLineInHMTL);
                currentLineInHMTL = input.readLine();
            }while (currentLineInHMTL != null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return rawHTMLPage;
    }
    public void writeToMDFile(){

    }


    public void parsingForLinksInString(String rawHTMLPage){
        String urlPattern = "(www|http:|https:)+[^\\s]+[\\w]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);

        while (matcher.find()){
            String subURL = matcher.group();
        }
    }

    public void parsingForHeadersInString(String rawHTMLPage){

    }
}

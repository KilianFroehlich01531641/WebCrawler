import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    /**
     * Global variables
     */
    int userMaximumPageDepth;
    ArrayList<ArrayList> queueList;
    ArrayList<String> depthLevelResults;
    String source;
    String target;
    ArrayList<String> alreadyVisitedURLs;

    /**
     * Constructor
     * @param userPageDepth - the number of times the crawler looks into each link from a page
     * @param userURL - the starting page
     * @param source - the language the web pages are to be expected
     * @param target - the language to translate into
     */
    public WebCrawler(int userPageDepth, String userURL, String source, String target){
        this.userMaximumPageDepth = userPageDepth;
        queueList = new ArrayList<>(userPageDepth);
        depthLevelResults = new ArrayList<>(userPageDepth);
        alreadyVisitedURLs = new ArrayList<String>();
        for (int i = 0; i < userPageDepth; i++) {
            queueList.add(new ArrayList<String>());
            depthLevelResults.add("");
        }

        try{
            queueList.get(0).add(userURL);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.source = source;
        this.target = target;
    }

    /**
     * Main method of this class. Initializes the file, crawls through the webpages and prints resulst into the file.
     */
    public void start(){
        initializeFile();

        for (int i = 0; i < userMaximumPageDepth; i++) {
            for (int j = 0; j < queueList.get(i).size() ; j++) {
                System.out.println("current link: " + queueList.get(i).get(j));
                crawl(queueList.get(i).get(j).toString(), i);
            }
            System.out.println("going deeper: Level " + (i+1)+ " now.");
        }

        writeResultsToFile();
    }

    /**
     * creates a MD file and writes header information into it.
     */
    public void initializeFile(){
        createMDFile();
        writeMDFile("<br>depth: "+ this.userMaximumPageDepth +"\n" +
                "<br>source language: "+ source +"\n" +
                "<br>target language: "+ target +"\n" +
                "<br>summary: \n\n");
    }

    /**
     * Takes the gloval val depthLevelResults and writes the content into the MD file.
     */
    public void writeResultsToFile(){
        int depthLevelCounter = 1;
        for (String result: depthLevelResults) {
            writeMDFile("---------Depth Level: " + depthLevelCounter + "--------\n");
            writeMDFile(result);
            depthLevelCounter++;
        }
    }

    /**
     * the method that handles the lookup and then starts to write the result into the file.
     * If the URL can't be accessed will handle the link accordingly.
     * @param urlString - the url to crwal through
     * @param currentItteration - the depth level that is currently worked on
     */
    public void crawl(String urlString, int currentItteration){
        try{
            URL url = new URL(urlString);
            String currentHTML = getRawHTMLFromURL(url);
            if(currentItteration+1 == userMaximumPageDepth || currentHTML == ""){
                currentURLResultWriting(currentHTML, currentItteration , urlString);
            }else{
                parsingForLinksInString(currentHTML, currentItteration, urlString);
            }
        }catch (Exception e){
            String currentHTML = "";
            currentURLResultWriting(currentHTML, currentItteration, urlString);
        }
    }

    /**
     * Opens Buffered reader and writes HTML File into string.
     * @param userURL - the URL of the page
     * @return - the page as a string or and empty string if it can't be read.
     */
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
            return rawHTMLPage;
        }
        return rawHTMLPage;
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

    /**
     * Writes the currently found link into the global depthLevelResults list.
     * Also removes link from queue and adds it to visited list.
     * @param rawHTMLPage - the page in which headers should be detected
     * @param currentDepth - current iteration
     * @param currentURL - the url of the HTML page
     */
    public void currentURLResultWriting(String rawHTMLPage, int currentDepth, String currentURL){
        queueList.get(currentDepth).remove(currentURL);
        alreadyVisitedURLs.add(currentURL);
        if(rawHTMLPage == ""){
            depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + "<br>--> broken link to <a>" + currentURL +"</a>\n\n");
        }else{
            depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + "<br>--> link to <a>" + currentURL +"</a>\n");
            parsingForHeadersInString(rawHTMLPage, currentDepth);
        }
    }

    /**
     * Look into the HTML of an page and searches for more links for future iterations.
     * @param rawHTMLPage - the HTML in which the links are searched
     * @param currentDepth - current iteration
     * @param currentURL - the url of the HTML page
     */
    public void parsingForLinksInString(String rawHTMLPage, int currentDepth, String currentURL){
        currentURLResultWriting(rawHTMLPage, currentDepth, currentURL);

        String urlPattern = "(www|http:|https:)+[^\\s\"\'\\*\\)]+[\\w]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);

        try{
            while (matcher.find()){
                String foundString = matcher.group();
                //have to check two lists now but decided to check here and not in the start() method because now I just don't add any links which are not supposed to be in here
                if(!(alreadyVisitedURLs.contains(foundString) || queueList.get(currentDepth+1).contains(foundString))){
                    queueList.get(currentDepth+1).add(foundString);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Searches for header tags in th HTML file.
     * @param rawHTMLPage - the HTML in which the links are searched
     * @param currentDepth - current iteration
     */
    public void parsingForHeadersInString(String rawHTMLPage, int currentDepth){
        String headerPattern = "(<h[1-6][^><]*>)[\\w]+(</h[1-6]>)";
        Pattern pattern = Pattern.compile(headerPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);
        String headerString = "";

        while (matcher.find()){
            String translation = translateHeaders(matcher.group());
            headerString += translation + "\n";
        }
        depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + headerString +"\n");
    }

    /**
     * Translates a string from given language to target language.
     * @param text - the string to translate
     * @param langTo - source language
     * @param langFrom - target language
     * @return a translated version of the input string
     */
    //Source:https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application
    public String translatingString(String text, String langTo, String langFrom){
        try{
            String urlStr = "https://script.google.com/macros/s/AKfycbw_Cn96JWZU7YA0aovyK50WuP-Bkqg023vHuHnRm_icFWpmOjkkXcNRQ1eFP8E3itQI/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;
            URL url = new URL(urlStr);
            StringBuilder response = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * removes header tags from string, translates it and gives it back with headers.
     * @param input - the string to translate
     * @return the translated version of the header
     */
    public String translateHeaders(String input){
         String[] headerParts = input.split(">");
         String[] headerPartsTwo = headerParts[1].split("<");
         String translatedString = translatingString(headerPartsTwo[0], target, source);
         if(translatedString != ""){
             return headerParts[0] + ">" + translatedString + "<" + headerPartsTwo[1] + ">";
         }
         return headerParts[0] + ">" + headerPartsTwo[0] + "<" + headerPartsTwo[1] + ">";
    }
}

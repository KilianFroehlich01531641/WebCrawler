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

    int userMaximumPageDepth;
    ArrayList<ArrayList> queueList;
    ArrayList<String> depthLevelResults;
    String source;
    String target;
    ArrayList<String> alreadyVisitedURLs;

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

    public void start(){
        createMDFile();
        writeMDFile("<br>depth: "+ this.userMaximumPageDepth +"\n" +
                "<br>source language: "+ source +"\n" +
                "<br>target language: "+ target +"\n" +
                "<br>summary: \n\n");
        for (int i = 0; i < userMaximumPageDepth; i++) {
            for (int j = 0; j < queueList.get(i).size() ; j++) {
                System.out.println("current link: " + queueList.get(i).get(j));
                crawl(queueList.get(i).get(j).toString(), i);
            }
            System.out.println("going deeper: Level " + (i+1)+ " now.");
        }
        int depthLevelCounter = 1;
        for (String result: depthLevelResults) {
            writeMDFile("---------Depth Level: " + depthLevelCounter + "--------\n");
            writeMDFile(result);
            depthLevelCounter++;
        }
    }

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
            //e.printStackTrace();
            return rawHTMLPage;
        }
        return rawHTMLPage;
    }

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

    public void writeMDFile(String input){
        try{
            FileWriter resultFile = new FileWriter("result_"+ userMaximumPageDepth + ".md", true);
            resultFile.write(input);
            resultFile.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

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

    public void parsingForLinksInString(String rawHTMLPage, int currentDepth, String currentURL){
        currentURLResultWriting(rawHTMLPage, currentDepth, currentURL);

        String urlPattern = "(www|http:|https:)+[^\\s\"\'\\*\\)]+[\\w]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);

        try{
            while (matcher.find()){
                String foundString = matcher.group();
                //dunno why but contains nor equals works to compare two strings as it will return always false
                if(!(alreadyVisitedURLs.contains(foundString))){
                    queueList.get(currentDepth+1).add(foundString);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

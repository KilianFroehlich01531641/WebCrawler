import jdk.internal.jline.internal.Urls;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    int userMaximumPageDepth;
    ArrayList<List> queueList;
    ArrayList<String> depthLevelResults;

    public WebCrawler(int userPageDepth, String userURL){
        this.userMaximumPageDepth = userPageDepth;
        queueList = new ArrayList<>(userPageDepth);
        depthLevelResults = new ArrayList<>(userPageDepth);
        for (int i = 0; i < userPageDepth; i++) {
            queueList.add(new ArrayList<String>());
            depthLevelResults.add("");
        }

        try{
            queueList.get(0).add(userURL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        createMDFile();
        writeMDFile("<br>depth: "+ this.userMaximumPageDepth +"\n" +
                "<br>source language: english\n" +
                "<br>target language: german\n" +
                "<br>summary: \n\n");
        for (int i = 0; i < userMaximumPageDepth; i++) {
            for (int j = 0; j < queueList.get(i).size() ; j++) {
                crawl((String) queueList.get(i).get(j), i);
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
        if(rawHTMLPage == ""){
            depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + "<br>--> broken link to <a>" + currentURL +"</a>\n\n");
        }else{
            depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + "<br>--> link to <a>" + currentURL +"</a>\n");
            parsingForHeadersInString(rawHTMLPage, currentDepth);
        }
    }

    public void parsingForLinksInString(String rawHTMLPage, int currentDepth, String currentURL){
        currentURLResultWriting(rawHTMLPage, currentDepth, currentURL);

        String urlPattern = "(www|http:|https:)+[^\\s^\"^\']+[\\w]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);

        try{
            while (matcher.find()){
                queueList.get(currentDepth+1).add(matcher.group());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parsingForHeadersInString(String rawHTMLPage, int currentDepth){
        String headerPattern = "(<h[1-6]>)[\\w]+(</h[1-6]>)";
        Pattern pattern = Pattern.compile(headerPattern);
        Matcher matcher = pattern.matcher(rawHTMLPage);
        String headerString = "";

        while (matcher.find()){
            headerString += matcher.group() + "\n";
        }
        depthLevelResults.set(currentDepth, depthLevelResults.get(currentDepth) + headerString +"\n");
    }
}

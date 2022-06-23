import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Translater {

    String sourceLanguage;
    String targetLanguage;

    Translater(String sourceLanguage, String targetLanguage){
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
    /**
     * Translates a string from given language to target language.
     * @param text - the string to translate
     * @return a translated version of the input string
     */
    //Source:https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application
    public String translatingString(String text){
        try{
            String urlStr = "https://script.google.com/macros/s/AKfycbxhSaQVD1N37KfkjM9tr_oXbDCx76bbakGbZqYKofTdlJZZNXty_4IYy0-yCYiKqTgwQw/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + sourceLanguage +
                    "&source=" + targetLanguage;
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
    public List<String> translateHeaders(List<String> input){
        for (String string: input) {
            string = translatingString(string);
        }
        return input;
    }
}

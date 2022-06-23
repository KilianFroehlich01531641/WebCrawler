import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class JsoupWrapper {
    
    public List<String> getElementTextByTagFromURL(String url, String tag) throws IOException{
        Elements tags = Jsoup.connect(url).get().select(tag);
        return tags.eachText();
    }
    public List<String> getLinkByTagFromURL(String url, String tag) throws IOException{
        Elements tags = Jsoup.connect(url).get().select(tag);
        return tags.eachAttr("abs:href");
    }

}

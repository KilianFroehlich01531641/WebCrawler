import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if(args.length < 4){
            System.out.println("Please start the crawler with the following command line format: -Depth, -Source Language, -Target Language, -URLs.");
        }else{
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 3; i < args.length; i++) {
                strings.add(args[i]);
            }
            WebCrawler webCrawler = new WebCrawler(strings, Integer.parseInt(args[0]), args[1], args[2]);
            webCrawler.start();
        }
    }
}

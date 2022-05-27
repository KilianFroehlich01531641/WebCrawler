public class Main {

    public static void main(String[] args) {
        if(args.length != 4){
            System.out.println("Please start the crawler with the following command line format: -URL, -Depth, -Source Language, -Target Language.");
        }else{
            WebCrawler webCrawler = new WebCrawler(args[0], Integer.parseInt(args[1]), args[2], args[3]);
            webCrawler.start();
        }
    }
}

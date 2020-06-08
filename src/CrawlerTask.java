import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class CrawlerTask implements Runnable {


    final static int AnyDepth = 0;

    private URLPool pool;

    private String prefix = "http";

    @Override
    public void run() {
        try {
            Scan();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CrawlerTask(URLPool pool) {
        this.pool = pool;
    }

    private  void  Scan() throws IOException, InterruptedException {
        while (true) {
            Process(pool.get());

        }
    }

    private void Process(URLDepthPair pair) throws IOException{

        URL url = new URL(pair.getURL());
        URLConnection connection = url.openConnection();

        String redirect = connection.getHeaderField("Location");
        if (redirect != null) {
            connection = new URL(redirect).openConnection();
        }

        pool.addProcessed(pair);
        if (pair.getDepth() == 0) return;


        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String input;
        while ((input = reader.readLine()) != null) {
            while (input.contains("a href=\"" + prefix)) {
                input = input.substring(input.indexOf("a href=\"" + prefix) + 8);
                String link = input.substring(0, input.indexOf('\"'));
                if(link.contains(" "))
                    link = link.replace(" ", "%20");

                if (pool.getNotProcessed().contains(new URLDepthPair(link, AnyDepth)) ||
                        pool.getProcessed().contains(new URLDepthPair(link, AnyDepth))) continue;
                pool.addNotProcessed(new URLDepthPair(link, pair.getDepth() - 1));
            }
        }
        reader.close();

    }


}

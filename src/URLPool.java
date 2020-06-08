import java.util.LinkedList;

public class URLPool {

    private LinkedList<URLDepthPair> processed = new LinkedList<URLDepthPair>();
    private LinkedList<URLDepthPair> notProcessed = new LinkedList<URLDepthPair>();
    private int depth;
    private int waiting;
    private int threads;

    public URLPool(String url, int depth, int threads) {
        notProcessed.add(new URLDepthPair(url, depth));
        this.depth = depth;
        this.threads = threads;
    }

    private boolean isEmpty() {
        if (notProcessed.size() == 0) return true;
        return false;
    }

    public void getSites() {
        System.out.println("Depth: " + depth);
        for (int i = 0; i < processed.size(); i++) {
            System.out.println( depth - processed.get(i).getDepth() + " " +  processed.get(i).getURL());
        }
        System.out.println("Links visited: " + processed.size());
    }

    public synchronized URLDepthPair get() throws InterruptedException {
        if (isEmpty()) {
            waiting++;
            if (waiting == threads) {
                getSites();
                System.exit(0);
            }
            wait();
        }
        return notProcessed.removeFirst();
    }
    public synchronized void addNotProcessed(URLDepthPair pair) {
        notProcessed.add(pair);
        if (waiting > 0) {
            waiting--;
            notify();
        }
    }

    public void addProcessed(URLDepthPair pair) {
        processed.add(pair);
    }

    public LinkedList<URLDepthPair> getProcessed()
    {
        return processed;
    }

    public LinkedList<URLDepthPair> getNotProcessed()
    {
        return notProcessed;
    }

}

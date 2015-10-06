import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        List<Node> nodeList = Initializer.createNetwork("InputGraphs/InputGraph1.json");

        nodeList.get(4).sendMessage(0, "Yahoo!");

        Thread.sleep(5000);
        System.exit(0);
    }
}

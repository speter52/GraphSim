import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        List<Node> nodeList = Initializer.createNetwork("InputGraphs/InputGraph1.json");

        nodeList.get(0).sendMessage(1, "Hi from 0");
        nodeList.get(1).sendMessage(0, "Hi from 1");
        nodeList.get(0).sendMessage(1, "Okay bye from 0");

        Thread.sleep(1000);
        System.exit(0);
    }
}

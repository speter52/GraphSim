import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to represent one node. Listens for and sends messages from/to other nodes while doing work.
 */
public class Node extends Thread
{
    /**
     * ID of this node.
     */
    private int nodeID;

    /**
     * Holds the array of message queues for each node in the network. Nodes receive messages by reading its queue and
     * send messages by adding to the recipient node's queue.
     */
    private LinkedBlockingQueue<String>[] communicationArray;

    /**
     * List of this node's neighbors.
     */
    private List<Integer> neighbors;

    /**
     * Dictionary of data in node.
     */
    private Map<String,Integer> data;

    /**
     * Primary Constructor.
     * @param communicationArray Array of message queues used for node communication
     */
    public Node(int nodeID, LinkedBlockingQueue<String>[] communicationArray, List<Integer> neighbors,
                Map<String,Integer> data)
    {
        this.nodeID = nodeID;

        this.communicationArray = communicationArray;

        this.neighbors = neighbors;

        this.data = data;
    }

    /**
     * Send a message to the specified node by adding the message to the appropriate queue.
     * @param receiverID ID of the node that will the message is being sent to
     * @param message Message content string
     */
    public void sendMessage(int receiverID, String message)
    {
        try
        {
            communicationArray[receiverID].put(message);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Send message to all neighbors of this node.
     * @param message Message content string
     */
    public void sendMessageToNeighbors(String message)
    {
        for (int neighbor : neighbors)
        {
            sendMessage(neighbor, message);
        }
    }

    /**
     * TEMPORARY: Figure out what to do after processing message
     * @param message
     */
    private void processMessage(String message)
    {
        System.out.println(nodeID + " received - " + message);

        sendMessageToNeighbors(message);
    }


    /**
     * Run method that does the work for the Node - processes messages from the message queue of this node.
     */
    public void run()
    {
        while(true)
        {
            try
            {
                String incomingMessage = communicationArray[nodeID].take();

                processMessage(incomingMessage);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }

    }
}

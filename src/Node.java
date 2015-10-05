import java.util.List;
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
     * Primary Constructor.
     * @param communicationArray
     */
    public Node(int nodeID, LinkedBlockingQueue<String>[] communicationArray, List<Integer> neighbors)
    {
        this.nodeID = nodeID;

        this.communicationArray = communicationArray;

        this.neighbors = neighbors;
    }

    /**
     * Send a message to the specified node by adding the message to the appropriate queue.
     * @param receiverID
     * @param message
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
     * @param message
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
        System.out.println(nodeID + " - " + message);
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

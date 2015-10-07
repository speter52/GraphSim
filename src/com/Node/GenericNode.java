package com.Node;

import com.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to represent one node. Listens for and sends messages from/to other nodes while doing work.
 */
public abstract class GenericNode extends Thread
{
    /**
     * ID of this node.
     */
    protected int nodeID;

    /**
     * Dictionary of data in node.
     */
    protected Map<String,Integer> data;

    /**
     * List of this node's neighbors.
     */
    protected List<Integer> neighbors;

    /**
     * Holds the array of message queues for each node in the network. Nodes receive messages by reading its queue and
     * send messages by adding to the recipient node's queue.
     */
    private LinkedBlockingQueue<String>[] communicationArray;

    /**
     * Abstract method implemented by user in CustomNode class to determine how the node should run.
     * @param message Received message
     */
    protected abstract void processMessage(String message);

    /**
     * Getter for node ID
     * @return nodeID
     */
    public int getNodeID()
    {
        return nodeID;
    }

    /**
     * Primary Constructor.
     * @param communicationArray Array of message queues used for node communication
     */
    public GenericNode(int nodeID, LinkedBlockingQueue<String>[] communicationArray, List<Integer> neighbors,
                       Map<String, Integer> data)
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
    public void sendMessage(int receiverID, Message message)
    {
        try
        {
            String messageString = message.encodeMessage();

            communicationArray[receiverID].put(messageString);
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
    public void sendMessageToNeighbors(Message message)
    {
        for (int neighbor : neighbors)
        {
            sendMessage(neighbor, message);
        }
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

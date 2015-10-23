package com.Network;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class to represent one node. Listens for and sends messages from/to other nodes while doing work.
 */
public abstract class GenericNode extends Thread
{
    /**
     * ID of this node.
     */
    protected int selfID;

    /**
     * Dictionary of data in node.
     */
    protected Map<String,Integer> data;

    /**
     * List of this node's neighbors.
     */
    protected ArrayList<Integer> neighbors;

    /**
     * Holds the array of message queues for each node in the network. Nodes receive messages by reading its queue and
     * send messages by adding to the recipient node's queue.
     */
    private MessagePasser messagePasser;

    protected abstract void startNode();

    protected abstract void processResponse(Message incomingMessage);

    /**
     * Process messages received by node
     * @param messageString
     */
    private void processMessage(String messageString)
    {
        Message incomingMessage = new Message(messageString);

        String messageType = incomingMessage.getArgument("Type");

        switch (messageType)
        {
            case "Start":
                startNode();
                break;

            case "Response":
                processResponse(incomingMessage);
                break;
        }
    }

    /**
     * Getter for node ID
     * @return selfID
     */
    public int getSelfID()
    {
        return selfID;
    }

    /**
     * Primary Constructor.
     * @param messagePasser Array of message queues used for node communication
     */
    public GenericNode(int nodeID, MessagePasser messagePasser, ArrayList neighbors,
                       Map<String, Integer> data)
    {
        this.selfID = nodeID;

        this.messagePasser = messagePasser;

        this.neighbors = neighbors;

        this.data = data;
    }

    /**
     * Send a message to the specified node by calling the messagePasser.
     * @param receiverID ID of the node that will the message is being sent to
     * @param message Message content string
     */
    public void sendMessage(int receiverID, Message message)
    {
        message.addArgument("receiverID", Integer.toString(receiverID));

        messagePasser.sendMessage(receiverID, message);
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
     * Send this node's values to all its neighbors.
     */
    public void sendValuesToNeighbors()
    {
        Message outgoingMessage = new Message();

        outgoingMessage.addArgument("Type", "Response");

        // TODO: Send all values
        outgoingMessage.addArgument("x", data.get("x").toString());

        outgoingMessage.addArgument("senderID", Integer.toString(selfID));

        sendMessageToNeighbors(outgoingMessage);
    }

    /**
     * Run method that processes messages from the message queue of this node.
     */
    public void run()
    {
        while(true)
        {
            String incomingMessage = messagePasser.waitAndRetrieveMessage(selfID);

            processMessage(incomingMessage);
        }
    }
}

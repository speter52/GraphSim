package com.Network;

import com.Helpers.OutputWriter.WriteType;
import com.Helpers.OutputWriter.WriteJob;
import com.Helpers.OutputWriter.WriterThread;
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
    private Map data;

    /**
     * The iteration number that this node is currently on.
     */
    protected int iterationNumber = 1;

    /**
     * Max number of iterations the algorithm should run
     */
    // Temporarily overridden in CustomNode class
    protected int iterationMax = 1000;

    /**
     * List of this node's neighbors.
     */
    protected ArrayList<Integer> neighbors;

    /**
     * Holds the array of message queues for each node in the network. Nodes receive messages by reading its queue and
     * send messages by adding to the recipient node's queue.
     */
    private MessagePasser messagePasser;

    /**
     * Thread that handles displaying output so node can continue processing work.
     */
    private WriterThread writer;

    /**
     * This function determines what the node will do when it first begins to start processing work. It
     * must be overridden by the user in the CustomNode class.
     */
    protected abstract void startNode();

    /**
     * This function determines what the node will do when it receives a message from another node. It must
     * be overridden by the user in the CustomNode class.
     * @param incomingMessage
     */
    protected abstract void processResponse(Message incomingMessage);

    /**
     * Getter for state variables of this node.
     * @param key
     * @return
     */
    protected Object getState(String key)
    {
        return data.get(key);
    }

    /**
     * Setter for state variables of this node.
     * @param key
     * @param value
     */
    protected void setState(String key, Object value)
    {
        data.put(key, value);

        ///////////////////TODO: TEMPORARY HACKY CODE, NEED TO GRAPH LILI'S DATA
        if(key == "x" && selfID == 0)
        {
            writer.addJob(new WriteJob(WriteType.FILE, value.toString()));

            writer.addJob(new WriteJob(WriteType.DATABASE, iterationNumber, Double.parseDouble(value.toString())));
        }
    }

    /**
     * Process messages received by node
     * @param messageString
     */
    private void processMessage(String messageString)
    {
        Message incomingMessage = new Message(messageString);

        String messageType = incomingMessage.getData("Type");

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
    public GenericNode(int nodeID, MessagePasser messagePasser, WriterThread writer, ArrayList neighbors,
                       Map data)
    {
        this.selfID = nodeID;

        this.messagePasser = messagePasser;

        this.writer = writer;

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
        message.addData("receiverID", Integer.toString(receiverID));

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
     * Send a specific key and value to all neighbors.
     * @param key
     * @param value
     */
    public void sendValueToNeighbors(String key, Object value)
    {
        Message outgoingMessage = new Message();

        outgoingMessage.addData("Type", "Response");

        outgoingMessage.addData(key, value.toString());

        outgoingMessage.addData("senderID", Integer.toString(selfID));

        sendMessageToNeighbors(outgoingMessage);
    }

    /**
     * Send this node's values to all its neighbors.
     */
    public void sendAllValuesToNeighbors()
    {
        Message outgoingMessage = new Message();

        outgoingMessage.addData("Type", "Response");

        //TODO: Specify type of data map
        //TODO: Seperate data values from message parameters
        for(Object dataEntry: data.entrySet())
        {
            Map.Entry entry = (Map.Entry)dataEntry;

            outgoingMessage.addData(entry.getKey().toString(), entry.getValue().toString());
        }

        outgoingMessage.addData("senderID", Integer.toString(selfID));

        sendMessageToNeighbors(outgoingMessage);
    }

    /**
     * Helper function for node that will print console output using the writer thread, allowing the primary
     * threads to continue processing work without interruption.
     * @param output
     */
    protected void printToConsole(String output)
    {
        writer.printToConsole(output);
    }

    /**
     * Run method that processes messages from the message queue of this node.
     */
    public void run()
    {
        while(iterationNumber < iterationMax+1)
        {
            String incomingMessage = messagePasser.waitAndRetrieveMessage(selfID);

            processMessage(incomingMessage);
        }

        printToConsole("Node " + selfID + " finished.");
    }
}

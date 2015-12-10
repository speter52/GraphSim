package com.Network;

import com.Helpers.OutputWriter.WriteType;
import com.Helpers.OutputWriter.WriteJob;
import com.Helpers.OutputWriter.WriterThread;
import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to represent one node. Listens for and sends messages from/to other nodes while doing work.
 */
public abstract class GenericNode extends Thread
{
    /**
     * Class to take messages from message passer and store them in internal message array by iteration
     * number of message.
     */
    private class Inbox extends Thread
    {
        /**
         * Mesage Passer that this Inbox listens to for incoming messages.
         */
        private MessagePasser messagePasser;

        /**
         * Internal structure of node that stores all incoming messages by iteration number.
         */
        private LinkedBlockingQueue<Message>[] incomingMessageArray;

        /**
         * Boolean that determines when the inbox thread should terminate.
         */
        private boolean isRunning = true;

        /**
         * Terminate inbox thread.
         */
        public void stopInbox()
        {
            isRunning = false;
        }

        /**
         * Primary constructor.
         * @param messagePasser
         * @param incomingMessageArray
         */
        public Inbox(MessagePasser messagePasser, LinkedBlockingQueue<Message>[] incomingMessageArray)
        {
            this.messagePasser = messagePasser;

            this.incomingMessageArray = incomingMessageArray;
        }

        /**
         * Run method of inbox thread. Finishes when isRunning is set to false.
         */
        public void run()
        {
            while(isRunning)
            {
                Message incomingMessage = new Message(messagePasser.waitAndRetrieveMessage(selfID));

                int iterationMessageSentFrom = Integer.parseInt(incomingMessage.getData("IterationNumber"));

                if((iterationMessageSentFrom - 1) < iterationMax)
                    incomingMessageArray[iterationMessageSentFrom - 1].add(incomingMessage);
            }
        }
    }

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
    protected int iterationMax = 100;

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
     * Structure that keeps track of all the messages received by the node and sorts by iteration number.
     */
    private LinkedBlockingQueue<Message>[] incomingMessageArray;

    /**
     * Class that sorts all the incoming messages by iteration number.
     */
    private Inbox inbox;

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

        writer.addJob(new WriteJob(WriteType.DATABASE, iterationNumber, selfID, key, Double.parseDouble(value.toString())));
    }

    /**
     * Process messages received by node
     * @param incomingMessage
     */
    private void processMessage(Message incomingMessage)
    {
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
                       Map data, int iterationMax)
    {
        this.selfID = nodeID;

        this.messagePasser = messagePasser;

        this.writer = writer;

        this.neighbors = neighbors;

        this.data = data;

        this.iterationMax = iterationMax;

        this.incomingMessageArray = new LinkedBlockingQueue[iterationMax];

        for(int i = 0; i < iterationMax; i++)
        {
            incomingMessageArray[i] = new LinkedBlockingQueue<>();
        }

        this.inbox = new Inbox(this.messagePasser, this.incomingMessageArray);

        this.inbox.start();
    }

    /**
     * Send a message to the specified node by calling the messagePasser.
     * @param receiverID ID of the node that will the message is being sent to
     * @param message Message content string
     */
    public void sendMessage(int receiverID, Message message)
    {
        message.addData("receiverID", Integer.toString(receiverID));

        message.addData("IterationNumber", Integer.toString(iterationNumber));

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
            try
            {
                Message incomingMessage = incomingMessageArray[iterationNumber-1].take();

                processMessage(incomingMessage);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }

        inbox.stopInbox();

        printToConsole("Node " + selfID + " finished.");
    }
}

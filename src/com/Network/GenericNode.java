package com.Network;

import com.Helpers.OutputWriter.WriteType;
import com.Helpers.OutputWriter.WriteJob;
import com.Helpers.OutputWriter.WriterThread;
import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;

import java.util.ArrayList;
import java.util.Map;
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
         * Message Passer that this Inbox listens to for incoming messages.
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
         * Run method of inbox thread. Listens for messages from the message passer and places them in the
         * appropriate queue that corresponds to the iteration the message was sent from. Messages from future
         * iterations come in when some nodes are ahead of others and must be tracked so the nodes still behave
         * synchronously.
         */
        public void run()
        {
            while(isRunning)
            {
                Message incomingMessage = new Message(messagePasser.waitAndRetrieveMessage(selfID));

                int iterationSentFrom = Integer.parseInt(incomingMessage.getData("IterationNumber"));

                if((iterationSentFrom) < iterationMax)
                    incomingMessageArray[iterationSentFrom].add(incomingMessage);
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
    protected int iterationNumber = 0;

    /**
     * Max number of iterations the algorithm should run
     */
    protected int iterationMax = 100;

    /**
     * Determines if the node has received a start message yet or not, and whether it can begin processing work.
     */
    private boolean isStarted = false;

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
     * An array of queues where each queue represents all the messages received by the node from a given iteration.
     * It is used to ensure the node only processes messages sent from the current iteration. In the event that
     * a message is sent from a future iteration since another node has gotten ahead, that message is stored for
     * later use.
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
     * Increments the iteration counter so the system can keep track of how many iterations of the algorithm
     * have elapsed.
     */
    protected void goToNextIteration()
    {
        // TODO: Keep function? Used to print debug messages at the end of an iteration

        iterationNumber++;
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
                isStarted = true;

                goToNextIteration();

                startNode();

                break;

            case "Response":
                if(isStarted)
                    processResponse(incomingMessage);
                else
                {
                    printToConsole("ERROR: Node %d received a response before being started - exiting.");

                    System.exit(1);
                }
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
     * Primary constructor for class.
     * @param nodeID ID of this node
     * @param messagePasser Structure used to pass messages between different nodes and clusters
     * @param writer Writer thread that manages saving system output.
     * @param neighbors List of neighbors of this node.
     * @param data Map of initial values for state variables.
     * @param iterationMax The maximum number of iterations the node will run for.
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

        // The 0th index of the incomingMessageArray will hold the start message
        this.incomingMessageArray = new LinkedBlockingQueue[iterationMax + 1];

        for(int i = 0; i < iterationMax+1; i++)
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
     * Helper function for node that will print console output using the writer thread, allowing the primary
     * threads to continue processing work without interruption.
     * @param output
     */
    protected void printToConsole(String output)
    {
        writer.printToConsole(output);
    }

    /**
     * Run method that processes messages from the message queue of this node that corresponds to the current iteration.
     */
    public void run()
    {
        // TODO: Since iterationNumber is 1-indexed, the loop should only finish when iterationNumber < iterationMax+1.
        // TODO: But looks like iterationNumber isn't incremented in the final iteration.
        while(iterationNumber < iterationMax)
        {
            try
            {
                Message incomingMessage = incomingMessageArray[iterationNumber].take();

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

package com.MessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to handle passing messages between different nodes.
 */
public class MessagePasser
{
    /**
     * A queue of message queues where each node in the cluster has its own message queue.
     */
    private LinkedBlockingQueue<String>[] communicationArray;

    /**
     * A dictionary that contains the index in the communicationArray that corresponds to a given node.
     */
    private Map<Integer,Integer> nodeIDToIndexMap;

    /**
     * Primary constructor that builds communication array from nodes in cluster.
     * @param nodesRepresentation Map that represents all the clusters in the network and their nodes
     */
    public MessagePasser(Map<Integer,Object> nodesRepresentation)
    {
        int index = 0;

        communicationArray = new LinkedBlockingQueue[nodesRepresentation.size()];

        nodeIDToIndexMap = new HashMap();

        // Initialize communication array with empty queues
        for(Integer key: nodesRepresentation.keySet())
        {
            nodeIDToIndexMap.put(index, key);

            communicationArray[index] = new LinkedBlockingQueue<String>();

            index++;
        }
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
            String messageString = message.serializeMessage();

            Integer receiverIndex = nodeIDToIndexMap.get(receiverID);

            communicationArray[receiverIndex].put(messageString);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Wait for a message to arrive on the selected node's queue and then retrieve that message.
     * @param nodeID
     * @return incomingMessage
     */
    public String waitAndRetrieveMessage(int nodeID)
    {
        String incomingMessage = null;

        try
        {
            int index = (Integer)nodeIDToIndexMap.get(nodeID);

            incomingMessage = communicationArray[index].take();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }

        return  incomingMessage;
    }
}

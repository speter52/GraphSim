package com.MessageHandler;

import com.Helpers.SocketInfo;
import com.Parser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to handle passing messages between different nodes.
 */
public class MessagePasser
{
    /**
     * Holds information of host and port that this cluster will listen to.
     */
    public SocketInfo socketInfo;

    /**
     * ID of the cluster this MessageParser is attached to.
     */
    public String selfClusterID;

    /**
     * A list of other clusters in the network.
     */
    public Set<String> otherClusters;

    /**
     * A queue of message queues where each node in the cluster has its own message queue.
     */
    private LinkedBlockingQueue<String>[] communicationArray;

    /**
     * A dictionary that contains the index in the communicationArray that corresponds to a given node.
     * Note: Only applies to the nodes in this cluster.
     */
    private Map<Integer,Integer> nodeIDToIndexMap;

    /**
     * A dictionary that maps a nodeID to the ID of the cluster it belongs to.
     */
    private Map<Integer,String> nodeToClusterMap;

    /**
     * A dictionary that maps a clusterID wih the port that it listens on.
     */
    private Map<String,SocketInfo> clusterSocketMap;

    /**
     * Primary constructor that builds communication array from nodes in cluster.
     * @param networkRepresentation Map that represents all the clusters in the network and their nodes
     */
    public MessagePasser(Map networkRepresentation)
    {
        Set<String> otherClusters = new HashSet(networkRepresentation.keySet());

        buildNodeClusterMaps(otherClusters, networkRepresentation);

        this.selfClusterID = Parser.getSelfClusterID(networkRepresentation);

        otherClusters.remove(this.selfClusterID);

        this.otherClusters = otherClusters;

        this.socketInfo = Parser.getSocketInfo(this.selfClusterID, networkRepresentation);

        Map<Integer,Object> nodesRepresentation = Parser.getNodesInCluster(this.selfClusterID, networkRepresentation);

        buildCommunicationArray(nodesRepresentation);
    }

    /**
     * Build a communication array that has an entry for every node on this cluster.
     * @param nodesRepresentation
     */
    private void buildCommunicationArray(Map<Integer, Object> nodesRepresentation)
    {
        int index = 0;

        this.communicationArray = new LinkedBlockingQueue[nodesRepresentation.size()];

        this.nodeIDToIndexMap = new HashMap();

        // Initialize communication array with empty queues
        for(Integer nodeID: nodesRepresentation.keySet())
        {
            nodeIDToIndexMap.put(nodeID, index);

            communicationArray[index] = new LinkedBlockingQueue<String>();

            index++;
        }
    }

    /**
     * Iterate through the network representation and map all the nodes in the network to their corresponding clusters
     * @param otherClusters
     * @param networkRepresentation
     */
    private void buildNodeClusterMaps(Set<String> otherClusters, Map networkRepresentation)
    {
        this.clusterSocketMap = new HashMap();

        this.nodeToClusterMap = new HashMap();

        for(String clusterID : otherClusters)
        {
            SocketInfo otherClusterSocketInfo = Parser.getSocketInfo(clusterID, networkRepresentation);

            this.clusterSocketMap.put(clusterID, otherClusterSocketInfo);

            Map<Integer,Object> nodeList = Parser.getNodesInCluster(clusterID, networkRepresentation);

            for(Integer nodeID : nodeList.keySet())
            {
                this.nodeToClusterMap.put(nodeID, clusterID);
            }
        }
    }

    private Socket createSocket(SocketInfo socketInfo)
    {
        try
        {
            return new Socket(socketInfo.getIP(), socketInfo.getPort());

        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * TODO: Figure out how to keep sockets open
     * Send a message to another cluster on the network.
     * @param clusterID ID of receiving cluster
     * @param message
     */
    public void sendMessageToCluster(String clusterID, Message message)
    {
        try
        {
            String messageString = message.serializeMessage();

            Socket receivingSocket = createSocket(clusterSocketMap.get(clusterID));

            DataOutputStream out = new DataOutputStream(receivingSocket.getOutputStream());

            out.writeUTF(messageString);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
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
            String receivingCluster = nodeToClusterMap.get(receiverID);

            // If the node is not in this cluster, send a message to the other cluster on the appropriate socket.
            // Else, to the appropriate entry in the messageQueue.
            if(otherClusters.contains(receivingCluster))
            {
                sendMessageToCluster(receivingCluster, message);
            }
            else
            {
                String messageString = message.serializeMessage();

                Integer receiverIndex = nodeIDToIndexMap.get(receiverID);

                communicationArray[receiverIndex].put(messageString);
            }
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
            int index = nodeIDToIndexMap.get(nodeID);

            incomingMessage = communicationArray[index].take();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }

        return  incomingMessage;
    }
}

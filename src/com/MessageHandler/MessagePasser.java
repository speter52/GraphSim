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
import java.util.concurrent.Semaphore;

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
     * A dictionary that maps a selfID to the ID of the cluster it belongs to.
     */
    private Map<Integer,String> nodeToClusterMap;

    /**
     * A dictionary that maps a clusterID wih the port that it listens on.
     */
    private Map<String,SocketInfo> clusterPortMap;

    /**
     * A dictionary that maps a clusterID with the Socket that is listening on.
     */
    private Map<String,Socket> clusterSocketMap;

    /**
     * Mutex for each node to ensure messages are added to the right queue in the order that they're sent.
     */
    private Semaphore[] mutexes;

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

        // TODO: Temp mutexes to try and eliminate deadlock
        mutexes = new Semaphore[nodesRepresentation.size()];

        for(int i = 0; i < nodesRepresentation.size(); i++)
        {
            mutexes[i] = new Semaphore(1);
        }
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
        this.clusterPortMap = new HashMap();

        this.clusterSocketMap = new HashMap<>();

        this.nodeToClusterMap = new HashMap();

        for(String clusterID : otherClusters)
        {
            SocketInfo otherClusterSocketInfo = Parser.getSocketInfo(clusterID, networkRepresentation);

            this.clusterPortMap.put(clusterID, otherClusterSocketInfo);

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
            //ex.printStackTrace();

            return null;
        }
    }

    /**
     * TODO: Store newly created clusters, don't keep opening and closing
     * Send a message to another cluster on the network.
     * @param clusterID ID of receiving cluster
     * @param message
     */
    public void sendMessageToCluster(String clusterID, Message message)
    {
        try
        {
            message.addData("senderCluster", selfClusterID);

            String messageString = message.serializeMessage();

            Socket receivingSocket = null;

            while(receivingSocket == null)
            {
                receivingSocket = createSocket(clusterPortMap.get(clusterID));

                if(receivingSocket == null)
                {
                    // TEMP Colors for console output
                    String ANSI_RESET = "\u001B[0m";
                    String ANSI_BOLD = "\u001B[1m";
                    String ANSI_YELLOW = "\u001B[33m";

                    System.out.println(ANSI_BOLD + ANSI_YELLOW + "Couldn't open socket to cluster, trying again..." + ANSI_RESET);

                    Thread.sleep(2000);
                }
            }

            DataOutputStream out = new DataOutputStream(receivingSocket.getOutputStream());

            out.writeUTF(messageString);
        }
        catch (Exception ex)
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
            // TODO: Need mutexes? Test speed with correctness
            mutexes[receiverID].acquire();

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

                int receiverIndex = nodeIDToIndexMap.get(receiverID);

                communicationArray[receiverIndex].put(messageString);
            }

            mutexes[receiverID].release();
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

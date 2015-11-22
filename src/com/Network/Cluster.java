package com.Network;

import com.Helpers.OutputWriter.WriterThread;
import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.Parser;

import java.util.*;

/**
 * Class that represents the cluster of nodes that will run on this instance.
 */
public class Cluster
{
    /**
     * ID of cluster
     */
    public String clusterID;

    /**
     * List of nodes in this cluster.
     */
    private Map<Integer,CustomNode> nodeList;

    /**
     * MessagePasser for all the nodes in this cluster.
     */
    public MessagePasser messagePasser;

    /**
     * Thread that displays output. Done in separate thread so actual work doesn't have to wait on it.
     */
    private final WriterThread writer;

    /**
     * Send a start message to all the nodes in this cluster.
     */
    public void startWork()
    {
        Message startMessage = new Message();

        startMessage.addData("Type", "Start");

        for (CustomNode node : nodeList.values())
        {
            node.sendMessage(node.getSelfID(), startMessage);
        }

        for(CustomNode node : nodeList.values())
        {
            try
            {
                node.join();
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }

        writer.printToConsole("Work complete, network exiting.");
    }

    /**
     * Iterates through the representation of the nodes and parses the entries to build each node.
     * @param nodesRepresentation JSON representation
     * @return List of nodes
     */
    private void buildNodes(Map<Integer,Object> nodesRepresentation)
    {
        nodeList = new HashMap<>();

        // Parse representation to build each node
        for(Map.Entry nodeEntry : nodesRepresentation.entrySet())
        {
            int nodeID = (Integer)nodeEntry.getKey();

            Map nodeDetails = (Map) nodeEntry.getValue();

            CustomNode newNode = Parser.parseNodeEntry(nodeID, nodeDetails, messagePasser, writer);

            newNode.start();

            nodeList.put(nodeID, newNode);
        }
    }

    /**
     * Primary constructor that uses a graph representation in YAML to build the network of nodes.
     * @param networkRepresentation
     * @return List of Nodes created.
     */
    public Cluster(Map networkRepresentation, MessagePasser messagePasser, WriterThread writer)
    {
        Map<Integer,Object> nodesRepresentation = Parser.getNodesInSelfCluster(networkRepresentation);

        this.messagePasser = messagePasser;

        this.writer = writer;

        buildNodes(nodesRepresentation);
    }
}

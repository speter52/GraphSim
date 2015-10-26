package com.Network;

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

            CustomNode newNode = Parser.parseNodeEntry(nodeID, nodeDetails, messagePasser);

            newNode.start();

            nodeList.put(nodeID, newNode);
        }
    }

    /**
     * Primary constructor that uses a graph representation in YAML to build the network of nodes.
     * @param networkRepresentation
     * @return List of Nodes created.
     */
    public Cluster(Map networkRepresentation, MessagePasser messagePasser)
    {
        Map<Integer,Object> nodesRepresentation = Parser.getNodesInSelfCluster(networkRepresentation);

        this.messagePasser = messagePasser;

        buildNodes(nodesRepresentation);
    }
}

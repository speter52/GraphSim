package com.Network;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.Network.CustomNode;
import com.Parser;

import java.util.*;

/**
 * Class that represents the cluster of nodes that will run on this instance.
 */
public class Cluster
{
    /**
     * List of nodes in this cluster.
     */
    private List<CustomNode> nodeList;

    /**
     * Send a start message to all the nodes in this cluster.
     */
    public void startNetwork()
    {
        Message startMessage = new Message();

        startMessage.addArgument("Type", "Start");

        for (CustomNode node : nodeList)
        {
            node.sendMessage(node.getNodeID(), startMessage);
        }
    }

    /**
     * Iterates through the representation of the nodes and parses the entries to build each node.
     * @param nodesRepresentation JSON representation
     * @param messagePasser of message queues for communication between nodes
     * @return List of nodes
     */
    private void buildNodes(Map<Integer,Object> nodesRepresentation, MessagePasser messagePasser)
    {
        nodeList = new ArrayList<CustomNode>();

        // Parse representation to build each node
        for(Map.Entry nodeEntry : nodesRepresentation.entrySet())
        {
            int nodeID = (Integer)nodeEntry.getKey();

            Map nodeDetails = (Map) nodeEntry.getValue();

            CustomNode newNode = Parser.parseNodeEntry(nodeID, nodeDetails, messagePasser);

            newNode.start();

            nodeList.add(nodeID, newNode);
        }
    }

    /**
     * Primary constructor that uses a graph representation in YAML to build the network of nodes.
     * @param inputFile
     * @return List of Nodes created.
     */
    public Cluster(String inputFile)
    {
        Map<Integer,Object> networkRepresentation = Parser.readYAMLFile(inputFile);

        Map<Integer,Object> nodesRepresentation = Parser.getNodesInSelfCluster(networkRepresentation);

        MessagePasser messagePasser = new MessagePasser(nodesRepresentation);

        buildNodes(nodesRepresentation, messagePasser);
    }
}

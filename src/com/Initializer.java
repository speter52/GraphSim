package com;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.Node.CustomNode;

import java.util.*;

/**
 * Class to read in graph representation from a json file and build the network of nodes.
 */
public class Initializer
{
    /**
     * Sends a start message to all the nodes in the network.
     * @param nodeList List of all nodes in network
     */
    public static void startNetwork(List<CustomNode> nodeList)
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
    private static List buildNodes(Map<Integer,Object> nodesRepresentation, MessagePasser messagePasser)
    {
        List<CustomNode> nodeList = new ArrayList<CustomNode>();

        // Parse representation to build each node
        for(Map.Entry nodeEntry : nodesRepresentation.entrySet())
        {
            int nodeID = (Integer)nodeEntry.getKey();

            Map nodeDetails = (Map) nodeEntry.getValue();

            CustomNode newNode = Parser.parseNodeEntry(nodeID, nodeDetails, messagePasser);

            newNode.start();

            nodeList.add(nodeID, newNode);
        }

        return nodeList;
    }

    /**
     * Uses a graph representation in a json to build the network of nodes.
     * @param inputFile Needs to be in the format - { "<Node ID>" : [<List of neighbor ID's>] }
     * @return List of Nodes created.
     */
    public static List createNetwork(String inputFile)
    {
        Map<Integer,Object> networkRepresentation = Parser.readYAMLFile(inputFile);

        Map<Integer,Object> nodesRepresentation = Parser.getNodesInSelfCluster(networkRepresentation);

        MessagePasser messagePasser = new MessagePasser(nodesRepresentation);

        List<CustomNode> nodeList = buildNodes(nodesRepresentation, messagePasser);

        return nodeList;
    }
}

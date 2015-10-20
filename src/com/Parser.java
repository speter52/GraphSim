package com;

import com.MessageHandler.MessagePasser;
import com.Node.CustomNode;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class to help parse input YAML files into node data structures.
 */
public class Parser
{
    /**
     * Read a YAML file into a Map that represents the structure of the graph.
     * @param inputFile
     * @return Map representation of graph
     */
    public static Map readYAMLFile(String inputFile)
    {
        Map<Integer,Object> networkRepresentation = null;

        try
        {
            Yaml yaml = new Yaml();

            networkRepresentation = (Map)yaml.load(new FileInputStream(new File(inputFile)));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return networkRepresentation;
    }

    /**
     * Retrieve all the nodes that belong to this instance's cluster.
     * @param networkRepresentation
     * @return Map of nodes in this cluster
     */
    public static Map getNodesInSelfCluster(Map networkRepresentation)
    {
        Map<Integer,Object> selfCluster = (Map)networkRepresentation.get("SelfCluster");

        return (Map)selfCluster.get("nodeList");
    }

    /**
     * Parse node entry to build Node structure.
     * @param nodeId
     * @param nodeDetails
     * @param messagePasser
     * @return CustomNode that was built
     */
    public static CustomNode parseNodeEntry(int nodeId, Map nodeDetails, MessagePasser messagePasser)
    {
        ArrayList neighbors = (ArrayList)nodeDetails.get("neighbors");

        Map data = (Map)nodeDetails.get("data");

        CustomNode newNode = new CustomNode(nodeId, messagePasser, neighbors, data);

        return newNode;
    }
}

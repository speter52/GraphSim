package com;

import com.Helpers.SocketInfo;
import com.MessageHandler.MessagePasser;
import com.Network.CustomNode;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
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
        Map networkRepresentation = null;

        try
        {
            Yaml yaml = new Yaml();

            networkRepresentation = (Map)yaml.load(new FileInputStream(new File(inputFile)));
        }
        catch(Exception ex)
        {
            System.out.println("Couldn't parse input file, exiting.");

            ex.printStackTrace();

            System.exit(0);
        }

        return networkRepresentation;
    }

    /**
     * Get all the nodes that will run on this cluster.
     * @param networkRepresentation
     * @return
     */
    public static Map getNodesInSelfCluster(Map networkRepresentation)
    {
        String selfClusterID = Parser.getSelfClusterID(networkRepresentation);

        return getNodesInCluster(selfClusterID, networkRepresentation);
    }

    /**
     * Retrieve all the nodes that belong to the given cluster
     * @param clusterID ID of the cluster to find
     * @param networkRepresentation
     * @return Map of nodes in this cluster
     */
    public static Map getNodesInCluster(String clusterID, Map networkRepresentation)
    {
        Map clusterEntry = (Map)networkRepresentation.get(clusterID);

        return (Map)clusterEntry.get("nodeList");
    }

    /**
     * Return the ID of the cluster that will run on this instance
     * @param networkRepresentation
     * @return clusterID
     */
    public static String getSelfClusterID(Map<String,Map> networkRepresentation)
    {
        for(Map.Entry<String,Map> clusterEntry: networkRepresentation.entrySet())
        {
            Map clusterInfo = clusterEntry.getValue();

            boolean isSelfCluster = (boolean)clusterInfo.get("isSelfCluster");

            if(isSelfCluster)
            {
                return clusterEntry.getKey();
            }
        }

        // Invalid config file, no self cluster found
        return null;
    }

    /**
     * Retrieves the ID's of all the clusters in the network.
     * @param networkRepresentation Representation of entire network
     * @param includeSelfCluster Include the cluster that runs on this instance or not
     * @return List of cluster ID's
     */
    public static List getClusterIDs(Map<String,Map> networkRepresentation, boolean includeSelfCluster)
    {
        List<String> clusterIDs = new ArrayList<>();

        for(Map.Entry<String,Map> clusterEntry: networkRepresentation.entrySet())
        {
            Map clusterInfo = clusterEntry.getValue();

            boolean isSelfCluster = (boolean)clusterInfo.get("isSelfCluster");

            if(isSelfCluster)
            {
                if(includeSelfCluster)
                {
                    clusterIDs.add(clusterEntry.getKey());
                }
            }
            else
            {
                clusterIDs.add(clusterEntry.getKey());
            }
        }

        return clusterIDs;
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

    /**
     * Get the ip and port information of a given cluster.
     * @param clusterID
     * @param networkRepresentation
     * @return SocketInfo object with information on given cluster
     */
    public static SocketInfo getSocketInfo(String clusterID, Map networkRepresentation)
    {
        Map clusterEntry = (Map)networkRepresentation.get(clusterID);

        String ip = (String)clusterEntry.get("ip");

        int port = (int)clusterEntry.get("port");

        return new SocketInfo(ip, port);
    }
}

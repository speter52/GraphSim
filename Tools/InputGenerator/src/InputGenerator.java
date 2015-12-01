import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to quickly generate different graph representations in YAML for input into the Simulator.
 */
public class InputGenerator
{
    /**
     * Number of nodes to represent in the input file.
     */
    private int numberOfNodes;

    /**
     * Number of neighbors each node should have.
     */
    private int numberOfNeighbors;

    /**
     * Number of partitions this cluster is divided into.
     */
    private int numberOfPartitions;

    /**
     * Number of groups of nodes that are connected to each other.
     */
    // TODO: Need to support generating multiple clusters
    private int numberOfClusters = 1;

    /**
     * State variables each node should hold.
     */
    private String[] stateVariables;

    /**
     * Primary constructor.
     * @param numberOfNodes
     * @param numberOfNeighbors
     */
    public InputGenerator(int numberOfNodes, int numberOfNeighbors, int numberOfPartitions, String[] stateVariables)
    {
        this.numberOfNodes = numberOfNodes;

        this.numberOfNeighbors = numberOfNeighbors;

        this.numberOfPartitions = numberOfPartitions;

        // TODO: Default to 1 for now
        this.numberOfClusters = 1;

        this.stateVariables = stateVariables;
    }

    /**
     * Generates the neighbors of this node.
     * @param nodeID
     * @return List of neighbors
     */
    private List generateNeighborsForNode(int nodeID)
    {
        List neighbors = new ArrayList();

        int numberOfNodesInPartition = numberOfNodes/numberOfPartitions;

        if(numberOfNeighbors >= numberOfNodesInPartition)
        {
            System.out.println("The number of neighbors has to be less than the number of nodes in a partition.");

            System.exit(1);
        }

        int partitionOfNode= nodeID/numberOfNodesInPartition;

        int partitionOffset = partitionOfNode*numberOfNodesInPartition;

        if(nodeID % numberOfNodesInPartition == 0)
        {
            int neighborToAdd = (nodeID-1) % numberOfNodes;

            if(neighborToAdd < 0) neighbors.add(neighborToAdd + numberOfNodes);

            else neighbors.add(neighborToAdd);
        }
        else if(nodeID % numberOfNodesInPartition == numberOfNodesInPartition-1)
        {
            neighbors.add((nodeID + 1) % numberOfNodes);
        }

        for(int i=0; i < numberOfNeighbors; i++)
        {
            int neighborIndexInPartition = ((nodeID+1+i) % (numberOfNodesInPartition));

            int currentNeighborID = neighborIndexInPartition + partitionOffset;

            if(currentNeighborID < numberOfNodes) neighbors.add(currentNeighborID);
        }

        return neighbors;
    }

    /**
     * Generates the state values of this node.
     * @param nodeID
     * @return A map of all the state values.
     */
    private Map generateDataForNode(int nodeID)
    {
        Map data = new HashMap();

        for(String stateVariable : stateVariables)
        {
            data.put(stateVariable, NodeDataGenerator.calculateInitialValue(stateVariable, nodeID));
        }

        return data;
    }

    /**
     * Generates the list of nodes with the given options
     */
    private Map generateNodeList()
    {
        Map nodeList = new HashMap();

        for(int currentNodeID = 0; currentNodeID < numberOfNodes; currentNodeID++)
        {
            Map currentNode = new HashMap();

            List currentNeighbors = generateNeighborsForNode(currentNodeID);
            currentNode.put("neighbors", currentNeighbors);

            Map currentData = generateDataForNode(currentNodeID);
            currentNode.put("data", currentData);

            nodeList.put(currentNodeID, currentNode);
        }

        return nodeList;
    }

    /**
     * Generates the list of clusters that will be represented in this network.
     */
    private Map generateClusterList()
    {
        Map clusterList = new HashMap();

        for(int currentClusterID = 0; currentClusterID < numberOfClusters; currentClusterID++)
        {
            Map currentCluster = new HashMap();

            // TODO: Currently only supports generating 1 cluster
            currentCluster.put("isSelfCluster", true);
            currentCluster.put("ip", "localhost");
            currentCluster.put("port", 2005);

            Map currentNodeList = generateNodeList();
            currentCluster.put("nodeList", currentNodeList);

            clusterList.put("Cluster" + currentClusterID, currentCluster);
        }

        return  clusterList;
    }

    /**
     * Generates an input file with the given options.
     */
    public void generateInput()
    {
        Map clusterList = generateClusterList();

        DumperOptions options = new DumperOptions();

        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);

        Yaml yaml = new Yaml(options);

        System.out.println(yaml.dump(clusterList));
    }
}

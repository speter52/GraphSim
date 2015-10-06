
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to read in graph representation from a json file and build the network of nodes.
 */
public class Initializer
{

    /**
     * Iterates through the JSON object and parses the entries to build each node.
     * @param networkRepresentation JSON representation
     * @param communicationArray Array of message queues for communication between nodes
     * @return List of nodes
     */
    private static List buildNodes(JSONObject networkRepresentation, LinkedBlockingQueue[] communicationArray)
    {
        List<Node> nodeList = new ArrayList<Node>();

        Iterator<String> keys = networkRepresentation.keys();

        // Parse JSON file to build each node
        // TODO: Implement allowing nodes to have any id instead of just increasing integer id's
        for(int nodeID = 0; nodeID < networkRepresentation.length(); nodeID++)
        {
            String currentKey = Integer.toString(nodeID);

            Node newNode = JSONParser.parseJSONObject(currentKey, networkRepresentation, communicationArray);

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
        JSONObject networkRepresentation = JSONParser.readJSONFile(inputFile);

        LinkedBlockingQueue<String>[] communicationArray = new LinkedBlockingQueue[networkRepresentation.length()];

        // Initialize communication array with empty queues
        for(int i = 0; i < networkRepresentation.length(); i++)
        {
            communicationArray[i] = new LinkedBlockingQueue<String>();
        }

        List<Node> nodeList = buildNodes(networkRepresentation, communicationArray);

        return nodeList;
    }
}

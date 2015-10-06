
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to read in graph representation from a json file and build the network of nodes.
 */
public class Initializer
{
    /**
     * Convert JSONArray to List.
     * @param array JSONArray to convert
     * @return Converted List
     */
    private static List convertJSONArrayToList(JSONArray array)
    {
        List newList = new ArrayList<String>();

        try
        {
            for (int i = 0; i < array.length(); i++)
            {
                newList.add(array.get(i));
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        return newList;
    }

    /**
     * Convert JSONObject to Map.
     * @param object JSONObject to convert
     * @return Converted Map
     */
    private static Map<String,Integer> convertJSONObjectToMap(JSONObject object)
    {
        Map<String, Integer> newDictionary = new HashMap<String, Integer>();

        try
        {
            Iterator<String> keys = object.keys();

            while (keys.hasNext())
            {
                String key = keys.next();

                int value = object.getInt(key);

                newDictionary.put(key,value);
            }
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();
        }

        return newDictionary;
    }

    /**
     * Read JSON file into JSONObject.
     * @param inputFile
     * @return JSONObject created from parsing input file
     */
    private static JSONObject readJSONFile(String inputFile)
    {
        JSONObject networkRepresentation = null;

        try
        {
            File file = new File(inputFile);

            FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];

            fis.read(data);

            fis.close();

            String jsonText = new String(data, "UTF-8");

            networkRepresentation = new JSONObject(jsonText);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return networkRepresentation;
    }

    /**
     * Parse parameters from JSON Object to build Node.
     * @param currentKey ID of Node
     * @param networkRepresentation JSON representation of network
     * @param communicationArray Array of message queues for node communication
     * @return New Node
     */
    private static Node parseJSONObject(String currentKey, JSONObject networkRepresentation,
                                        LinkedBlockingQueue[] communicationArray )
    {
        try
        {
            List<Integer> neighbors;

            Map<String,Integer> data;

            JSONObject nodeObject = networkRepresentation.getJSONObject(currentKey);

            JSONArray neighborsArray = nodeObject.getJSONArray("Neighbors");

            neighbors = convertJSONArrayToList(neighborsArray);

            JSONObject dataObject= nodeObject.getJSONObject("Data");

            data = convertJSONObjectToMap(dataObject);

            Node newNode = new Node(Integer.parseInt(currentKey), communicationArray, neighbors, data);

            return newNode;
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

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

            Node newNode = parseJSONObject(currentKey, networkRepresentation, communicationArray);

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
        JSONObject networkRepresentation = readJSONFile(inputFile);

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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to read in graph represention from a json file and build the network of nodes.
 */
public class Initializer
{
    /**
     * Convert JSONArray to List
     * @param array
     * @return
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
     * Read JSON file into JSONObject.
     * @param inputFile
     * @return
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

    private static List buildNodes(JSONObject networkRepresentation, LinkedBlockingQueue[] communicationArray)
    {
        List<Node> nodeList = new ArrayList<Node>();

        Iterator<String> keys = networkRepresentation.keys();

        // Parse JSON file to build each node
        // TODO: Implement allowing nodes to have any id instead of just increasing integer id's
        for(int nodeID = 0; nodeID < networkRepresentation.length(); nodeID++)
        {
            String currentKey = Integer.toString(nodeID);

            List<Integer> neighbors = new ArrayList<>();

            try
            {
                JSONArray neighborsArray = networkRepresentation.getJSONArray(currentKey);

                neighbors = convertJSONArrayToList(neighborsArray);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            Node newNode = new Node(nodeID,communicationArray,neighbors);

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

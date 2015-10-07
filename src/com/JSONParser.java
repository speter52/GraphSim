package com;

import com.Node.CustomNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class JSONParser
{
    /**
     * Convert JSONArray to List.
     * @param array JSONArray to convert
     * @return Converted List
     */
    public static List convertJSONArrayToList(JSONArray array)
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
     * @param valuesAsInts If true, store the values of the map as Ints, otherwise store as Strings
     * @return Converted Map
     */
    public static Map convertJSONObjectToMap(JSONObject object, boolean valuesAsInts)
    {
        // Values are stored as ints when the object is referring to the data of a node. The values
        // are stored as Strings when the object is referring to a message sent between nodes which can
        // have data along with other commands.
        // TODO: Refactor function, need map values as both Strings and Ints?
        Map newDictionary = valuesAsInts ? new HashMap<String,Integer>() : new HashMap<String,String>();

        try
        {
            Iterator<String> keys = object.keys();

            while (keys.hasNext())
            {
                String key = keys.next();

                if(valuesAsInts)
                {
                    int value = object.getInt(key);

                    newDictionary.put(key, value);
                }
                else
                {
                    String value = object.getString(key);

                    newDictionary.put(key, value);
                }
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
    public static JSONObject readJSONFile(String inputFile)
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
     * TODO: Consider using YAML or other user-friendly serialization language for network config file
     * @param currentKey ID of Node
     * @param networkRepresentation JSON representation of network
     * @param communicationArray Array of message queues for node communication
     * @return New Node
     */
    public static CustomNode parseJSONObject(String currentKey, JSONObject networkRepresentation,
                                LinkedBlockingQueue[] communicationArray)
    {
        try
        {
            List<Integer> neighbors;

            Map<String,Integer> data;

            JSONObject nodeObject = networkRepresentation.getJSONObject(currentKey);

            JSONArray neighborsArray = nodeObject.getJSONArray("Neighbors");

            neighbors = convertJSONArrayToList(neighborsArray);

            JSONObject dataObject= nodeObject.getJSONObject("Data");

            boolean storeValuesAsInts = true;

            data = convertJSONObjectToMap(dataObject, storeValuesAsInts);

            CustomNode newNode = new CustomNode(Integer.parseInt(currentKey), communicationArray, neighbors, data);

            return newNode;
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}

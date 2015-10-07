package com;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent and handle messages.
 */
public class Message
{
    /**
     * Stores the contents of the message in a key-value map.
     */
    private Map<String,String> messageContents;

    /**
     * Primary constructor that creates an empty message.
     */
    public Message()
    {
        messageContents = new HashMap<String,String>();
    }

    /**
     * Consructor that builds a message from a JSON string representation.
     */
    public Message(String message)
    {
        try
        {
            JSONObject object = new JSONObject(message);

            boolean storeValuesAsInts = false;

            messageContents = JSONParser.convertJSONObjectToMap(object, storeValuesAsInts);
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();

            messageContents = null;
        }
    }

    /**
     * Add a new key-value argument to the message.
     * @param key
     * @param value
     */
    public void addArgument(String key, String value)
    {
        messageContents.put(key, value);
    }

    /**
     * Return the value of an argument given its key.
     * @param key Key of argument
     * @return Value of argument
     */
    public String getArgument(String key)
    {
        return messageContents.get(key);
    }

    /**
     * TODO: Override toString() method?
     * Converts the message into a JSON string representation.
     * @return Message string
     */
    public String encodeMessage()
    {
        JSONObject object = new JSONObject(messageContents);

        return object.toString();
    }

}

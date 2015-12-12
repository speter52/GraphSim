package com.MessageHandler;

import org.yaml.snakeyaml.Yaml;

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
     * Consructor that builds a message from a YAML string representation.
     */
    public Message(String message)
    {
        Yaml yaml = new Yaml();

        messageContents = (Map)yaml.load(message);
    }

    /**
     * Add a new key-value argument to the message.
     * @param key
     * @param value
     */
    public void addData(String key, String value)
    {
        messageContents.put(key, value);
    }

    /**
     * Return the value of an argument given its key.
     * @param key Key of argument
     * @return Value of argument
     */
    public String getData(String key)
    {
        return messageContents.get(key);
    }

    /**
     * Converts the message into a YAML string representation.
     * @return Message string
     */
    public String serializeMessage()
    {
        Yaml yaml = new Yaml();

        String serializedMessage = yaml.dump(messageContents);

        return serializedMessage;
    }

    @Override
    public String toString()
    {
        return this.serializeMessage();
    }
}

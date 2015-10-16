package com.Node;

import com.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class where user defines the behavior of each node.
 */
public class CustomNode extends GenericNode
{
    public CustomNode(int nodeID, LinkedBlockingQueue<String>[] communicationArray, List<Integer> neighbors,
                      Map<String, Integer> data)
    {
        super(nodeID, communicationArray, neighbors, data);
    }

    /**
     * USER WRITTEN CODE BELOW TO PROCESS MESSAGES AND COMMUNICATE WITH NEIGHBORS
     * USER NEEDS TO IMPLEMENT ABSTRACT processMessage() METHOD FROM SUPERCLASS
     */

    // Colors for console output
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BOLD = "\u001B[1m";

    /**
     * List to keep track of the response values received from other nodes.
     */
    private List<Integer> responsesReceived = new ArrayList<Integer>();

    /**
     * Calculate and return the average of a list of ints.
     * @param listOfInts
     * @return average
     */
    private int calculateAverageOfList(List<Integer> listOfInts)
    {
        int sum = 0;

        for(double item : listOfInts)
        {
            sum += item;
        }

        return sum/listOfInts.size();
    }

    /**
     * When a response is received from another node, add it to the response history. Once responses are
     * received from all the neighbors, average that and mark it as your new value for x.
     * @param incomingMessage
     */
    @Override
    protected void processResponse(Message incomingMessage)
    {
        int responseValue = Integer.parseInt(incomingMessage.getArgument("x"));

        System.out.println(ANSI_GREEN + "Node " + nodeID + " received " + responseValue + " from Node " +
                incomingMessage.getArgument("ID") + ANSI_RESET);

        responsesReceived.add(responseValue);

        // Once all the neighboring responses are received, average them and update x
        if(responsesReceived.size() >= neighbors.size())
        {
            int averageOfResponses = calculateAverageOfList(responsesReceived);

            data.put("x", averageOfResponses);

            System.out.println(ANSI_BLUE + ANSI_BOLD + "Node " + nodeID + " updated value of X to " +
                    averageOfResponses + ANSI_RESET);

            responsesReceived.clear();

            sendValuesToNeighbors();
        }
    }

    /**
     * Process the start message that says the node can begin doing work
     */
    @Override
    protected void startNode()
    {
        sendValuesToNeighbors();
    }
}

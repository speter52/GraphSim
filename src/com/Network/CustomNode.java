package com.Network;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class where user defines the behavior of each node.
 */
public class CustomNode extends GenericNode
{
    public CustomNode(int nodeID, MessagePasser messagePasser, ArrayList neighbors,
                      Map data)
    {
        super(nodeID, messagePasser, neighbors, data);
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

    private List<Double> responsesReceived = new ArrayList<>();

    /**
     * Calculate and return the average of a list of ints.
     * @param listOfInts
     * @return average
     */
    private double calculateAverageOfList(List<Double> listOfInts)
    {
        double sum = 0;

        for(double item : listOfInts)
        {
            sum += item;
        }

        return sum/listOfInts.size();
    }

    @Override
    protected void processResponse(Message incomingMessage)
    {
        Double xReceived = Double.parseDouble(incomingMessage.getData("x"));

        responsesReceived.add(xReceived);

        System.out.println(ANSI_GREEN + "Node " + selfID + " received " + xReceived + " from Node " +
                incomingMessage.getData("senderID") + ANSI_RESET);

        if(responsesReceived.size() >= neighbors.size())
        {
            double newX = calculateAverageOfList(responsesReceived);

            double y = (Double)data.get("y");

            newX = newX + y;

            data.put("x", newX);

            System.out.println(ANSI_BLUE + ANSI_BOLD + "Node " + selfID + " updated value of X to " +
                    newX + ANSI_RESET);

            responsesReceived.clear();

            algorithm1Prologue();
        }
    }

    @Override
    protected void startNode()
    {
        algorithm1Prologue();
    }

    private void algorithm1Prologue()
    {
        double t = (double)data.get("t");

        double x = (double)data.get("x");

        t = t + 1;

        double step2Subtractor = selfID*2 + 1;

        double y = (1./t)*(x - step2Subtractor);

        data.put("t",t);

        data.put("y",y);

        sendValuesToNeighbors();
    }
}

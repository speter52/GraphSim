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

    private void algorithm1Prologue()
    {
        double t = (double)data.get("t");

        double x = (double)data.get("x");

        //Step 1: t <- t + 1
        t = t + 1;

        double step2Subtractor = selfID*2 + 1;

        //Step 2: y <- (1/t)(x - {1,3,5,7,9})
        double y = (1./t)*(x - step2Subtractor);

        data.put("t",t);

        data.put("y",y);

        data.put("xPlusy",x + y);

        // Step 3: Send x + y to all neighbors
        sendValuesToNeighbors();
    }

    @Override
    protected void processResponse(Message incomingMessage)
    {
        // Step 4: Receive messages from all neighbors
        Double xPlusy = Double.parseDouble(incomingMessage.getData("xPlusy"));

        responsesReceived.add(xPlusy);

        System.out.println("Node " + selfID + " received " + xPlusy + " from Node " +
                incomingMessage.getData("senderID"));

        if(responsesReceived.size() >= neighbors.size())
        {
            // Step 5: After all messages are received from other nodes, set x to average
            double newX = calculateAverageOfList(responsesReceived);

            data.put("x", newX);

            System.out.println("Node " + selfID + " updated value of X to " +
                    newX);

            responsesReceived.clear();

            // Next iteration
            algorithm1Prologue();
        }
    }

    @Override
    protected void startNode()
    {
        algorithm1Prologue();
    }

}

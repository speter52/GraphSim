package com.Helpers.OutputWriter;

import com.Helpers.OutputWriter.WriteType;

/**
 * Class that holds the information for output from the system and where it should go.
 */
public class WriteJob
{
    public WriteType type;

    public String output;

    /**
     * TODO: Temporary members to store state values in memory, need to expand to support multiple state variables and
     * TODO: nodes.
     */
    public int iterationNumber;

    public int nodeId;

    public String stateVariable;

    public double stateValue;

    public WriteJob(WriteType type, String output)
    {
        this.type = type;

        this.output = output;
    }

    // TODO: Temporary constructor to speed up development of webapp
    public WriteJob(WriteType type, int iterationNumber, int nodeID, String stateVariable, double stateValue)
    {
        this.type = type;

        this.iterationNumber = iterationNumber;

        this.nodeId = nodeID;

        this.stateVariable = stateVariable;

        this.stateValue = stateValue;
    }
}

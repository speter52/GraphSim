package com.Helpers;

import com.Helpers.Enums.WriteType;

import java.util.Map;

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

    public double stateValue;

    public WriteJob(WriteType type, String output)
    {
        this.type = type;

        this.output = output;
    }

    // TODO: Temporary constructor to speed up development of webapp
    public WriteJob(WriteType type, int iterationNumber, double stateValue)
    {
        this.type = type;

        this.iterationNumber = iterationNumber;

        this.stateValue = stateValue;
    }
}

package com.Helpers;

import com.Helpers.Enums.WriteType;

/**
 * Class that holds the information for output from the system and where it should go.
 */
public class WriteJob
{
    public WriteType type;

    public String output;

    public WriteJob(WriteType type, String output)
    {
        this.type = type;

        this.output = output;
    }
}

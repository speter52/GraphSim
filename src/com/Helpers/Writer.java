package com.Helpers;

import com.Helpers.Enums.WriteType;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread that will write output to a file so that the primary threads can continue sending messages.
 */
public class Writer extends Thread
{
    /**
     * Queue to hold all the jobs for the Writer.
     */
    private LinkedBlockingQueue<WriteJob> outputQueue;

    /**
     * Primary constructor.
     */
    public Writer()
    {
        this.outputQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Adds a write job to the output queue.
     * @param job
     */
    public void addJob(WriteJob job)
    {
        outputQueue.add(job);
    }

    /**
     * Process a write job from the outputQueue.
     * @param job
     */
    private void processJob(WriteJob job)
    {
        switch (job.type)
        {
            case FILE:
                writeToFile(job);
        }
    }

    /**
     * TODO: Open and close files more efficiently, maybe have a list of all files that are currently being tracked
     * TODO: Make output file path a command line parameter
     * Writes output to a file.
     * @param job
     */
    private void writeToFile(WriteJob job)
    {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt", true))))
        {
            out.println(job.output);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void run()
    {
        while(true)
        {
            try
            {
                processJob(outputQueue.take());
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }

        }

    }
}

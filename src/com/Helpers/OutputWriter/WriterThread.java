package com.Helpers.OutputWriter;

import com.Helpers.ConfigReader;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread that will write output to a file so that the primary threads can continue sending messages.
 */
public class WriterThread extends Thread
{
    /**
     * Holds all the configuration values for the application.
     */

    private ConfigReader configValues;
    /**
     * Queue to hold all the jobs for the WriterThread.
     */
    private LinkedBlockingQueue<WriteJob> outputQueue;

    /**
     * State values to be outputted to DB after simulation is complete.
     * TODO: Expand structure to support values from different state values and different nodes.
     */
    private Map<Integer, Double> valuesForDB;
    /**
     * Primary constructor.
     */
    public WriterThread()
    {
        this.outputQueue = new LinkedBlockingQueue<>();

        this.valuesForDB = new HashMap<>();

        this.configValues = new ConfigReader();
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
                break;

            case DATABASE:
                valuesForDB.put(job.iterationNumber, job.stateValue);
                break;

            case CONSOLE:
                System.out.println(job.output);
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

    /**
     * Write all values in memory to DB.
     * TODO: Need to expand to support multiple state variables and different nodes.
     */
    public void pushValuesToDB()
    {
        try
        {
            String url = configValues.getDatabaseInstance() + "?allowMultipleQueries=true";
            String user = configValues.getDatabaseUser();
            String password = configValues.getDatabasePassword();
            String table = configValues.getDatabaseTable();

            printToConsole("Saving results to database.");

            Connection dbConnection = DriverManager.getConnection(url, user, password);

            Statement dropStatement = dbConnection.createStatement();

            dropStatement.execute(String.format("DROP TABLE IF EXISTS %s;", table));

            Statement createStatement = dbConnection.createStatement();

            createStatement.execute(String.format("CREATE TABLE %s(IterationNumber int, Val float(8,4));", table));

            Statement insertStatements = dbConnection.createStatement();

            for(Map.Entry entry: valuesForDB.entrySet())
            {
                insertStatements.addBatch(String.format("INSERT INTO %s (IterationNumber, Val) VALUES (" + entry.getKey() +
                        ", " + entry.getValue() + ")", table));
            }

            insertStatements.executeBatch();

            dbConnection.close();
        }
        catch(SQLException ex)
        {
            printToConsole("Error in writing files to database.");

            ex.printStackTrace();
        }
    }

    /**
     * Adds a job to the queue that will print a message to the console.
     * @param output
     */
    public void printToConsole(String output)
    {
        addJob(new WriteJob(WriteType.CONSOLE, output));
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

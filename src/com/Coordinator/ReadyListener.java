package com.Coordinator;

import com.MessageHandler.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket; import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Thread class that will listen for Ready messages from the other clusters that will indicate the network can begin
 * doing work.
 */
public class ReadyListener extends Thread
{
    /**
     * Socket that this cluster listens on for ready messages from other clusters.
     */
    private ServerSocket listeningSocket;

    /**
     * A set of the other clusters in the network that should send ready messages before starting the network.
     */
    Set<String> otherClusters;

    /**
     * A list of all the clusters that have sent ready messages.
     */
    List<String> clusterResponsesReceived;

    /**
     * Constructor for ReadyListener.
     * @param otherClusters ID's of other clusters in network
     */
    public ReadyListener(Set<String> otherClusters, ServerSocket listeningSocket)
    {
        try
        {
            this.listeningSocket = listeningSocket;

            this.otherClusters = otherClusters;

            this.clusterResponsesReceived = new ArrayList<>();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Run method that will do the actual listening.
     */
    public void run()
    {
        try
        {
            System.out.println("Waiting for other clusters...");

            while(clusterResponsesReceived.size() < otherClusters.size())
            {
                Socket socketToClient = listeningSocket.accept();

                DataInputStream in = new DataInputStream(socketToClient.getInputStream());

                Message incomingMessage = new Message(in.readUTF());

                String sendingCluster = incomingMessage.getArgument("senderCluster");

                System.out.println("Ready received from " + sendingCluster);

                clusterResponsesReceived.add(sendingCluster);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}

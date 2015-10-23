package com.Coordinator;

import com.Helpers.SocketInfo;
import com.MessageHandler.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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
     * @param socketInfo Port to listen on
     * @param otherClusters ID's of other clusters in network
     */
    public ReadyListener(SocketInfo socketInfo, Set<String> otherClusters)
    {
        try
        {
            this.listeningSocket = new ServerSocket();

            this.listeningSocket.setReuseAddress(true);

            this.listeningSocket.bind(new InetSocketAddress(socketInfo.getPort()));

            this.otherClusters = otherClusters;

            this.clusterResponsesReceived = new ArrayList<>();
        }
        catch (IOException ex)
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

            Socket socketToClient = listeningSocket.accept();

            DataInputStream in = new DataInputStream(socketToClient.getInputStream());

            while(clusterResponsesReceived.size() <= otherClusters.size())
            {
                Message incomingMessage = new Message(in.readUTF());

                String sendingCluster = incomingMessage.getArgument("clusterID");

                System.out.println("Ready received from " + sendingCluster);

                clusterResponsesReceived.add(sendingCluster);
            }

            socketToClient.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}

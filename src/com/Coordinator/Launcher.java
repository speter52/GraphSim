package com.Coordinator;

import com.Helpers.Writer;
import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.Network.Cluster;
import com.Network.CustomNode;
import com.Parser;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;

/**
 * Class to control and configure node network.
 */
public class Launcher
{
    /**
     * TODO: Refactor function
     * Build nodes in this cluster, wait for all the other clusters to start up, and then send out start messages
     * to nodes on this cluster.
     * @param inputFile
     */
    public static void launchNetwork(String inputFile)
    {
        try
        {
            // Read input file
            Map networkRepresentation = Parser.readYAMLFile(inputFile);

            if(networkRepresentation == null)
            {
                System.out.println("Invalid input file. Exiting.");

                System.exit(0);
            }

            MessagePasser messagePasser = new MessagePasser(networkRepresentation);

            // Creating listening socket to use for ready messages and messages from other nodes
            ServerSocket listeningSocket = new ServerSocket();

            listeningSocket.setReuseAddress(true);

            listeningSocket.bind(new InetSocketAddress(messagePasser.socketInfo.getPort()));

            // Start listening for Ready messages from other clusters
            Thread readyListener = new ReadyListener(messagePasser.otherClusters, listeningSocket);

            readyListener.start();

            // Creating output writer
            Writer writer = new Writer();

            writer.start();

            Cluster selfCluster = new Cluster(networkRepresentation, messagePasser, writer);

            // Let other clusters in network know this cluster is ready
            notifyNetworkReady(messagePasser);

            // Wait for all clusters to become ready
            readyListener.join();

            // Begin listening for messages from nodes of other clusters
            Thread networkListener = new NetworkListener(messagePasser, listeningSocket);

            networkListener.start();

            // Let cluster start processing work
            selfCluster.startWork();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Send messages to other clusters in network indicating that this cluster is created and ready.
     * @param messagePasser
     */
    private static void notifyNetworkReady(MessagePasser messagePasser)
    {
        for(String receivingClusterID: messagePasser.otherClusters)
        {
            Message readyMessage = new Message();

            readyMessage.addData("Type", "Ready");

            readyMessage.addData("SendingCluster", messagePasser.selfClusterID);

            messagePasser.sendMessageToCluster(receivingClusterID, readyMessage);
        }

    }
}

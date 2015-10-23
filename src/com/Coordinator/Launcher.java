package com.Coordinator;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.Network.Cluster;
import com.Parser;

import java.util.Map;

/**
 * Class to control and configure node network.
 */
public class Launcher
{

    /**
     * Build nodes in this cluster, wait for all the other clusters to start up, and then send out start messages
     * to nodes on this cluster.
     * @param inputFile
     */
    public static void launchNetwork(String inputFile)
    {
        try
        {
            Map networkRepresentation = Parser.readYAMLFile(inputFile);

            MessagePasser messagePasser = new MessagePasser(networkRepresentation);

            Thread readyListener = new ReadyListener(messagePasser.socketInfo, messagePasser.otherClusters);

            readyListener.start();

            Cluster selfCluster = new Cluster(networkRepresentation, messagePasser);

            notifyNetworkReady(messagePasser);

            readyListener.join();

            Thread networkListener = new NetworkListener(messagePasser.socketInfo, messagePasser);

            networkListener.start();

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

            readyMessage.addArgument("Type", "Ready");

            readyMessage.addArgument("SendingCluster", messagePasser.selfClusterID);

            messagePasser.sendMessageToCluster(receivingClusterID, readyMessage);
        }

    }
}

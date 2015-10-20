package com;

import com.Network.Cluster;

/**
 * Class to control and configure node network.
 */
public class Coordinator
{
    /**
     * Build nodes in this cluster, wait for all the other clusters to start up, and then send out start messages
     * to nodes on this cluster.
     * @param inputFile
     */
    public static void launchNetwork(String inputFile)
    {
        Cluster selfCluster = new Cluster(inputFile);

        selfCluster.startNetwork();
    }
}

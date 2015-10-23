package com.Coordinator;

import com.Helpers.SocketInfo;
import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;
import com.sun.corba.se.spi.activation.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that will listen for messages from nodes in other clusters that are being sent to nodes in this cluster.
 */
public class NetworkListener extends Thread
{
    /**
     * Socket that this server listens to for messages from other clusters.
     */
    private ServerSocket listeningSocket;

    /**
     * Input stream from listening socket.
     */
    private DataInputStream in;

    /**
     * MessagePasser that will handle sending messages to the nodes in this cluster.
     */
    private MessagePasser messagePasser;

    public NetworkListener(SocketInfo socketInfo, MessagePasser messagePasser, ServerSocket listeningSocket)
    {
        try
        {
            System.out.println("Port: " + socketInfo.getPort());

            /*
            this.listeningSocket = new ServerSocket();

            this.listeningSocket.setReuseAddress(true);

            this.listeningSocket.bind(new InetSocketAddress(socketInfo.getPort()));
            */
            this.listeningSocket = listeningSocket;

            this.messagePasser =  messagePasser;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Run method that will listen for messages from other clusters
     */
    public void run()
    {
        try
        {
            Socket socketToClient = listeningSocket.accept();

            DataInputStream in = new DataInputStream(socketToClient.getInputStream());

            while(true)
            {
                    Message incomingMessage = new Message(in.readUTF());

                    int receiverID = Integer.parseInt(incomingMessage.getArgument("receiverID"));

                    String sendingCluster = incomingMessage.getArgument("senderCluster");

                    System.out.println("Message received from " + sendingCluster + " sending to Node " + receiverID);

                    messagePasser.sendMessage(receiverID, incomingMessage);
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

    }
}

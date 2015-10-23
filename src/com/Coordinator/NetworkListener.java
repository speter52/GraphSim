package com.Coordinator;

import com.MessageHandler.Message;
import com.MessageHandler.MessagePasser;

import java.io.DataInputStream;
import java.io.IOException;
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
     * MessagePasser that will handle sending messages to the nodes in this cluster.
     */
    private MessagePasser messagePasser;

    /**
     * Primary constructor
     * @param messagePasser
     * @param listeningSocket
     */
    public NetworkListener(MessagePasser messagePasser, ServerSocket listeningSocket)
    {
        try
        {
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
            while(true)
            {
                Socket socketToClient = listeningSocket.accept();

                DataInputStream in = new DataInputStream(socketToClient.getInputStream());

                Message incomingMessage = new Message(in.readUTF());

                int receiverID = Integer.parseInt(incomingMessage.getArgument("receiverID"));

                messagePasser.sendMessage(receiverID, incomingMessage);
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

    }
}

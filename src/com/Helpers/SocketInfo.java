package com.Helpers;

/**
 * Helper class to store socket IP and Port number information to commmunicate between clusters.
 */
public class SocketInfo
{
    private String ip;

    private int port;

    public String getIP()
    {
        return this.ip;
    }

    public int getPort()
    {
        return this.port;
    }

    public SocketInfo(String ip, int port)
    {
        this.ip = ip;

        this.port = port;
    }
}

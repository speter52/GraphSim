package com;

import com.Coordinator.Launcher;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        if(args.length == 1)
        {
            Launcher.launchNetwork(args[0]);
        }
        else
        {
            System.out.println("Need input YAML file that represents the nodes in the network.");
        }

        System.exit(0);
    }
}

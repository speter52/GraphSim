package com;

import com.Coordinator.Launcher;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        if(args.length == 2)
        {
            // 1st argument - Input YAML file, 2nd argument - Maximum number of iterations
            Launcher.launchNetwork(args[0], Integer.parseInt(args[1]));
        }
        else
        {
            System.out.println("Need input YAML file that represents the nodes in the network, " +
                                "followed by maximum number of iterations.");
        }

        System.exit(0);
    }
}

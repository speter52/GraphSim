package com;

import com.Coordinator.Launcher;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        Launcher.launchNetwork(args[0]);

        System.exit(0);
    }
}

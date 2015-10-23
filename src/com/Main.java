package com;

import com.Coordinator.Launcher;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        Launcher.launchNetwork("InputGraphs/InputGraph1.yml");

        Thread.sleep(50000);

        System.exit(0);
    }
}

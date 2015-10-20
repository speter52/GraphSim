package com;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        Coordinator.launchNetwork("InputGraphs/InputGraph1.yml");

        Thread.sleep(50000);

        System.exit(0);
    }
}

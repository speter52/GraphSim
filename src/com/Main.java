package com;

import com.Node.CustomNode;

import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        List<CustomNode> nodeList = Initializer.createNetwork("InputGraphs/InputGraph2.json");

        Initializer.startNetwork(nodeList);

        Thread.sleep(50000);

        System.exit(0);
    }
}

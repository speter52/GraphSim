package com;

import com.Node.CustomNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws InterruptedException
    {
        List<CustomNode> nodeList = Initializer.createNetwork("InputGraphs/InputGraph2.json");

        Initializer.startNetwork(nodeList);

        Thread.sleep(50000);

        System.exit(0);
    }
}

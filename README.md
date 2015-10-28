# SeniorThesis

I'm designing a framework for my Senior Thesis to abstract away the implementation of a network of nodes for the study of distributed algorithms. The goal is to allow a user to easily define how a node behaves, and then launch a network of nodes to visualize the algorithm running.

# Initializing the System

The system will take as inputs a YAML file representing the network of nodes, and two functions that need to be overridden to tell the system how each node should behave.

# Input YAML Graph

The input file will contain the information for each node, which "cluster" they belong to if they will be communicating with nodes on another process, and the corresponding socket information to listen to.

A valid format will consist of first the "clusterID", which contains keys for "ip", "port", a "nodeList" with all of the information on the cluster's nodes, and a boolean "isSelfCluster" parameter that indicates which cluster will be represented by this instance of the program. There can only be one cluster where this parameter is set to true, the information on the other clusters is needed so the system knows which cluster to send a message to when the receiving node is not located locally.

The "nodeList" contains a list of nodes whose keys are the integer ID's of the node, and values contain an array of integers representing that nodes "neighbors", along with a "data" map that contains all the state variables for each node and their corresponding initial values. There is no limit on the number of state variables, which can be strings or numbers.

Here's a valid input file:

```
Cluster0:
  isSelfCluster: true
  ip: localhost
  port: 20005
  nodeList:
    0:
      neighbors: [1,2]
      data:
        x: 2.0
        y: 0.0
        t: 0.0
    1:
      neighbors: [0,2]
      data:
        x: 4.0
        y: 0.0
        t: 0.0
    2:
      neighbors: [0,1]
      data:
        x: 6.0
        y: 0.0
        t: 0.0
```

# Node Behavior

The behavior of each node is defined in the CustomNode class, which inherits from the GenericNode class. CustomNode needs to implement the abstract functions startNode() and processResponse(), and also comes with additional functions that should help make defining the node behavior straightforward.

What the node should do to begin procressing work is defined in startNode(). That is the first function that gets executed once the node receives a Start message from the network indicating all the other nodes are ready. The variable iterationMax should be set here and determines the maximum number of iterations the system should execute. After that, the node will execute the code in processResponse() everytime it receives a message from a neighbor.

Samples node behaviors are in GraphInputs/Algorithms/.

# Helper Functions

int selfID : The ID of this node.

int iterationNumber : The iteration counter that must be incremented everytime an iteration is complete. Initialized to 0.

int iterationMax : The maximum number of iterations that should be executed. For now, it must be set in startNode().

List<Integer> neighbors : A list of the ID's of the neighbors to this node. neighbors.size() can used to find the number of neighbors.

Object getState(String key) : Returns the value of a state variable, and null if not present. The value needs to be casted to the appropriate type.

void setState(String key, Object value) : Updates the value of a state variable, or adds it if not already present.

void sendValueToNeighbors(String key, Object value) : Sends the corresponding key and value to all its neighbors.

void sendAllValuesToNeighbors() : Sends all the keys and values of this node to all of its neighbors.


# Starting The System

First give the start script execute permissions:
  
    chmod +X build_and_run.sh
    
Then run the script with an input YAML file that containts the graph representation.

    ./build_and_run.sh <InputGraph.yml>

If the node behavior compiles, then the system will launch and run the specified number of iterations.




For any questions or suggestions email speter3@illinois.edu

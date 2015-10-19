# SeniorThesis

I'm designing a framework for my Senior Thesis to abstract away the implementation of a network of nodes for the study of distributed algorithms. The goal is for a user to easily define how a node behaves, and then launch a network of nodes to visualize the algorithm running.

Currently the system reads a json representation of a graph of nodes and then initializes them. The user implements the startNode() and processResponse() functions in the CustomNode class to determine what exactly the node should do.

More details to follow.

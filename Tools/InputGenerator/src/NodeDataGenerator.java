/**
 * Class where user enters code to generate initial state values for each node.
 */
public class NodeDataGenerator
{
    /**
     * Function that determines the initial value each state variable of a node is initialized to.
     * @param stateVariable
     * @param nodeID
     * @return
     */
    public static double calculateInitialValue(String stateVariable, int nodeID)
    {
        switch (stateVariable){
            case "x": return nodeID*2+2;

            default: return 0;
        }
    }
}

/**
 * Class where user enters code to generate initial state values for each node.
 */
public class NodeDataGenerator
{
    /**
     * Function that determines the initial value a given node should for each state variable.
     * @param stateVariable
     * @param nodeID
     * @return
     */
    public static double calculateInitialValue(String stateVariable, int nodeID)
    {
        if(stateVariable.equals("x")) return nodeID*2+2;

        else return 0;
    }
}

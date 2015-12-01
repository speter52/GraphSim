import java.util.Arrays;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        int numberOfNodes = Integer.parseInt(args[0]);

        int numberOfNeighbors = Integer.parseInt(args[1]);

        int numberOfPartitions = Integer.parseInt(args[2]);

        String[] stateVariables = Arrays.copyOfRange(args, 2, args.length);

        InputGenerator generator = new InputGenerator(numberOfNodes, numberOfNeighbors,
                                                        numberOfPartitions, stateVariables);

        generator.generateInput();
    }
}

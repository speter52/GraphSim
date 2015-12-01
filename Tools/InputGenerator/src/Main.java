public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        InputGenerator generator = new InputGenerator(5,2,1, new String[]{"x","y","t"});

        generator.generateInput();
    }
}

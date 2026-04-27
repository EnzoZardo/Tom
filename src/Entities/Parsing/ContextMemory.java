package Entities.Parsing;

public class ContextMemory
{
    private int loopDepth;
    private int functionDepth;

    public int enterLoop()
    {
        return ++loopDepth;
    }

    public int outLoop()
    {
        return --loopDepth;
    }

    public int enterFunction()
    {
        return ++functionDepth;
    }

    public int outFunction()
    {
        return --functionDepth;
    }

    public int function()
    {
        return functionDepth;
    }

    public int loop()
    {
        return loopDepth;
    }
}

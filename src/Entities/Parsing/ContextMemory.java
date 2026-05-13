package Entities.Parsing;

public class ContextMemory
{
    private int loopDepth;
    private int functionDepth;
    private ContextMemory()
    {
        loopDepth = 0;
        functionDepth = 0;
    }

    public static ContextMemory create()
    {
        return new ContextMemory();
    }

    public void enterLoop()
    {
        ++loopDepth;
    }

    public void outLoop()
    {
         --loopDepth;
    }

    public void enterFunction()
    {
        ++functionDepth;
    }

    public void outFunction()
    {
        --functionDepth;
    }

    public boolean inFunction()
    {
        return functionDepth > 0;
    }

    public boolean inLoop()
    {
        return loopDepth > 0;
    }
}

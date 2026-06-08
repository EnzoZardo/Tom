package Entities.Parsing;

public class ContextMemory
{
    private int loopDepth;
    private int functionDepth;
    private int classDepth;

    private ContextMemory()
    {
        loopDepth = 0;
        classDepth = 0;
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

    public void enterClass()
    {
        ++classDepth;
    }

    public void outClass()
    {
        --classDepth;
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

    public boolean inClass()
    {
        return classDepth > 0;
    }
}

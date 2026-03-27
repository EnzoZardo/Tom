package Exceptions;

public class NullConstantException extends RuntimeException
{
    public NullConstantException()
    {
        super("Cannot declare an undefined constant.");
    }

    public static void ThrowIf(boolean condition)
    {
        if (condition)
        {
            throw new NullConstantException();
        }
    }

}

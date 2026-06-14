package Entities.Exceptions.Parser;

public class NullConstantException extends RuntimeException
{
    public NullConstantException()
    {
        super("Não podemos declarar uma constante sem valor.");
    }

    public static void throwIf(boolean condition)
    {
        if (condition)
        {
            throw new NullConstantException();
        }
    }
}

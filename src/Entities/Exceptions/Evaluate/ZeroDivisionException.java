package Entities.Exceptions.Evaluate;

public class ZeroDivisionException extends EvaluatingException
{
    public ZeroDivisionException()
    {
        super("Não é possível dividir por zero.");
    }

    public static void ThrowIfZero(Number number)
    {
        if (number.floatValue() == 0)
        {
            throw new ZeroDivisionException();
        }
    }
}

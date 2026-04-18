package Entities.Exceptions.Evaluate;

public abstract class EvaluatingException extends RuntimeException
{
    public EvaluatingException(String message)
    {
        super(message);
    }
}

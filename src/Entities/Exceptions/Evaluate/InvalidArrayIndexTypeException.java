package Entities.Exceptions.Evaluate;

public class InvalidArrayIndexTypeException extends EvaluatingException
{
    public InvalidArrayIndexTypeException()
    {
        super("Somente são aceitados números inteiros para indexar uma lista.");
    }
}

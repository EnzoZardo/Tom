package Entities.Exceptions.Parser;


public abstract class ParsingException extends RuntimeException
{
    public ParsingException(String message)
    {
        super(message);
    }
}

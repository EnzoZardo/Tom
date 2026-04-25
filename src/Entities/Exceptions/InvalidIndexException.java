package Entities.Exceptions;

public class InvalidIndexException extends RuntimeException
{
    public InvalidIndexException(String message)
    {
        super(message);
    }
}

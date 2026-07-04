package Entities.Exceptions;

public class FileNotExistsException extends RuntimeException
{
    public FileNotExistsException(String message)
    {
        super(message);
    }
}

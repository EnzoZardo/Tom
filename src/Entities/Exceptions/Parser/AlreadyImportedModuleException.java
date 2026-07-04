package Entities.Exceptions.Parser;

public class AlreadyImportedModuleException extends RuntimeException
{
    public AlreadyImportedModuleException(String message)
    {
        super(message);
    }
}

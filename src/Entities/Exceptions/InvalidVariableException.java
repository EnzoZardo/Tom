package Entities.Exceptions;

public class InvalidVariableException extends RuntimeException
{
    public InvalidVariableException(String name)
    {
        super(String.format("Cannot resolve variable %s. As it does not exist.", name));
    }
}

package Entities.Exceptions;

public class AlreadyDeclaredVariableException extends Exception
{
    public AlreadyDeclaredVariableException(String name)
    {
        super(String.format("Cannot declare variable %s. As it's already declared.", name));
    }
}
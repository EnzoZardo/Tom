package Entities.Exceptions;

public class NotDeclaredVariableException extends Exception
{
    public NotDeclaredVariableException(String name)
    {
        super(String.format("Cannot assign variable %s. As it's was not declared.", name));
    }
}
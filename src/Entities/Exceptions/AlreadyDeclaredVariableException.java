package Entities.Exceptions;

public class AlreadyDeclaredVariableException extends Exception
{
    public AlreadyDeclaredVariableException(String name)
    {
        super(String.format("Não podemos declarar a variável %s. Ela já foi declarada.", name));
    }
}
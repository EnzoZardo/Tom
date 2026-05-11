package Entities.Exceptions;

public class InvalidVariableException extends RuntimeException
{
    public InvalidVariableException(String name)
    {
        super(String.format("Não conseguimos resolver a variável %s. Ela não existe.", name));
    }
}

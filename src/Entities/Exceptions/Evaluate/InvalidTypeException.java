package Entities.Exceptions.Evaluate;

public class InvalidTypeException extends RuntimeException
{
    public InvalidTypeException(String name)
    {
        super(String.format("Não conseguimos resolver o tipo %s. Ele não existe.", name));
    }
}
package Entities.Exceptions.Evaluate;

public class InvalidBinaryOperation extends EvaluatingException
{
    public InvalidBinaryOperation()
    {
        super("Expressão binária inválida.");
    }

    public InvalidBinaryOperation(String message)
    {
        super(message);
    }
}

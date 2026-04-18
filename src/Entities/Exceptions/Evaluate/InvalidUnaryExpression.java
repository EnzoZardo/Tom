package Entities.Exceptions.Evaluate;

public class InvalidUnaryExpression extends EvaluatingException
{
    public InvalidUnaryExpression()
    {
        super("Expressão unária inválida.");
    }
}

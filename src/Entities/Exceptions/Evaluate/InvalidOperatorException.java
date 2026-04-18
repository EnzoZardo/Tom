package Entities.Exceptions.Evaluate;

public class InvalidOperatorException extends EvaluatingException
{
    public InvalidOperatorException(String operator)
    {
        super(String.format("Operador %s é inválido nesse contexto", operator));
    }
}

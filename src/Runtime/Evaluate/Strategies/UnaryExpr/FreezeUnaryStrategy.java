package Runtime.Evaluate.Strategies.UnaryExpr;

import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Abstractions.Runtime.FreezableValue;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.Evaluate.InvalidUnaryExpression;

public class FreezeUnaryStrategy implements UnaryExprStrategy
{
    @Override
    public RuntimeValue evaluate(RuntimeValue right, String operator)
    {
        if (!right.isFreezable())
        {
            throw new InvalidUnaryExpression("Valor do tipo informado não pode ser congelado.");
        }

        return ((FreezableValue) right).freezeMe();
    }
}

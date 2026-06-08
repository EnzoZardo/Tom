package Runtime.Evaluate.Strategies.UnaryExpr;

import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.BooleanValue;

public class NotUnaryStrategy implements UnaryExprStrategy
{
    @Override
    public RuntimeValue evaluate(RuntimeValue right, String operator)
    {
        return BooleanValue.create(right.not());
    }
}

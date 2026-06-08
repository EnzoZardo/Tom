package Runtime.Evaluate.Strategies.BinaryExpr.String;

import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.StringValue;

public class StringAdditionStrategy implements StringBinaryExprStrategy
{
    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left)
    {
        return StringValue.create(left.toString() + right.toString());
    }
}
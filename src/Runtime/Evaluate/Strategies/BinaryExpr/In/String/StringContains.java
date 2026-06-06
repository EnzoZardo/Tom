package Runtime.Evaluate.Strategies.BinaryExpr.In.String;

import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.StringValue;

public abstract class StringContains<T extends RuntimeValue> extends Contains<StringValue>
{
    protected StringContains(StringValue right, T left)
    {
        super(right, left);
    }
}

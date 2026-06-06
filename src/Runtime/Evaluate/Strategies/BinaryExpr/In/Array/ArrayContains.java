package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.ArrayValue;

public abstract class ArrayContains<T extends RuntimeValue> extends Contains<ArrayValue>
{
    protected ArrayContains(ArrayValue right, T left) {
        super(right, left);
    }
}

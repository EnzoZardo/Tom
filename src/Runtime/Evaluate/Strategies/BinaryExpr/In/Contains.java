package Runtime.Evaluate.Strategies.BinaryExpr.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.BooleanValue;

public abstract class Contains<T extends RuntimeValue>
{
    protected final T right;
    protected final RuntimeValue left;

    protected Contains(T right, RuntimeValue left)
    {
        this.right = right;
        this.left = left;
    }

    public abstract BooleanValue has();
}

package Runtime.Evaluate.Strategies.BinaryExpr.In.Object;

import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.ObjectValue;

public abstract class ObjectContains<T extends RuntimeValue> extends Contains<ObjectValue>
{
    protected ObjectContains(ObjectValue right, T left) {
        super(right, left);
    }
}


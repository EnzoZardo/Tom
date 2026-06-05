package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;

public abstract class InArrayBase
{
    protected final ArrayValue right;

    public InArrayBase(ArrayValue right) {
        this.right = right;
    }

    public abstract BooleanValue evaluate(RuntimeValue left);
}

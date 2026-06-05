package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;

public class InArrayBoolean extends InArrayBase
{
    public InArrayBoolean(ArrayValue right)
    {
        super(right);
    }

    @Override
    public BooleanValue evaluate(RuntimeValue left)
    {
        BooleanValue value = (BooleanValue) left;
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                .anyMatch(x -> x.type == ValueType.Boolean && value.equals(x))
        );
    }
}

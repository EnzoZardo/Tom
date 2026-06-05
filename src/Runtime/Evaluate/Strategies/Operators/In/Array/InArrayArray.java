package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.ObjectValue;

public class InArrayArray extends InArrayBase
{
    public InArrayArray(ArrayValue right)
    {
        super(right);
    }

    @Override
    public BooleanValue evaluate(RuntimeValue left)
    {
        ObjectValue value = (ObjectValue) left;
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                .anyMatch(x -> x.type == ValueType.Array && value.equals(x))
        );
    }
}

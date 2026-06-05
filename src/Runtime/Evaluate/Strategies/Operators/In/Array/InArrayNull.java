package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;

public class InArrayNull extends InArrayBase
{
    public InArrayNull(ArrayValue right)
    {
        super(right);
    }

    @Override
    public BooleanValue evaluate(RuntimeValue left)
    {
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                    .anyMatch(x -> x.type == ValueType.Null)
        );
    }
}
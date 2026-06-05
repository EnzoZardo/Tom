package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.StringValue;

public class InArrayString extends InArrayBase
{
    public InArrayString(ArrayValue right)
    {
        super(right);
    }

    @Override
    public BooleanValue evaluate(RuntimeValue left)
    {
        StringValue value = (StringValue) left;
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                .anyMatch(x -> x.type == ValueType.String && value.equals(x))
        );
    }
}

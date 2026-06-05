package Runtime.Evaluate.Strategies.Operators.In.Array;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.NumericValue;

public class InArrayNumeric extends InArrayBase
{
    public InArrayNumeric(ArrayValue right)
    {
        super(right);
    }

    @Override
    public BooleanValue evaluate(RuntimeValue left)
    {
        NumericValue value = (NumericValue) left;
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                .anyMatch(x -> x.type == ValueType.Numeric && value.equals(x))
        );
    }
}

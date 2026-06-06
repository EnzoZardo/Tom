package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;

public class ArrayContainsBoolean extends ArrayContains<BooleanValue>
{
    public ArrayContainsBoolean(ArrayValue right, BooleanValue left)
    {
        super(right, left);
    }

    @Override
    public BooleanValue has()
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

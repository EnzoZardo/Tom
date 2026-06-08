package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;


public class ArrayContainsArray extends ArrayContains<ArrayValue>
{
    public ArrayContainsArray(ArrayValue right, ArrayValue left)
    {
        super(right, left);
    }

    @Override
    public BooleanValue has()
    {
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                .anyMatch(x -> x.type == ValueType.Array && left.equals(x))
        );
    }
}

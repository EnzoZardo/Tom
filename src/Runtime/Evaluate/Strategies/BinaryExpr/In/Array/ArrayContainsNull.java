package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.NullValue;

public class ArrayContainsNull extends ArrayContains<NullValue>
{
    public ArrayContainsNull(ArrayValue right)
    {
        super(right, NullValue.create());
    }

    @Override
    public BooleanValue has()
    {
        return BooleanValue.create(
            right.items
                .values()
                .stream()
                    .anyMatch(x -> x.type == ValueType.Null)
        );
    }
}
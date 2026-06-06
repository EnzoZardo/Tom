package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.ObjectValue;

public class ArrayContainsObject extends ArrayContains<ObjectValue>
{
    public ArrayContainsObject(ArrayValue right, ObjectValue left)
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
                .anyMatch(x -> x.type == ValueType.Object && left.equals(x))
        );
    }
}
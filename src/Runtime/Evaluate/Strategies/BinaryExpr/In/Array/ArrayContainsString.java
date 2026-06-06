package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.StringValue;

public class ArrayContainsString extends ArrayContains<StringValue>
{
    public ArrayContainsString(ArrayValue right, StringValue left)
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
                .anyMatch(x -> x.type == ValueType.String && left.equals(x))
        );
    }
}

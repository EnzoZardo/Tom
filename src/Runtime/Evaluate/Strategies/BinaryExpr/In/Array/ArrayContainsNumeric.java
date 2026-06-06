package Runtime.Evaluate.Strategies.BinaryExpr.In.Array;

import Entities.Enums.Runtime.ValueType;
import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.NumericValue;

public class ArrayContainsNumeric extends ArrayContains<NumericValue>
{
    public ArrayContainsNumeric(ArrayValue right, NumericValue left)
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
                .anyMatch(x -> x.type == ValueType.Numeric && left.equals(x))
        );
    }
}

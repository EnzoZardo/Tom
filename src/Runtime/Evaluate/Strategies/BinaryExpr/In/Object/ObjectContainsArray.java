package Runtime.Evaluate.Strategies.BinaryExpr.In.Object;

import Runtime.Values.ArrayValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.ObjectValue;

public class ObjectContainsArray extends ObjectContains<ArrayValue>
{
    private static final int ENTRY_SIZE = 2;

    public ObjectContainsArray(ObjectValue right, ArrayValue left)
    {
        super(right, left);
    }

    @Override
    public BooleanValue has()
    {
        ArrayValue value = (ArrayValue) left;
        if (value.items.size() > ENTRY_SIZE)
        {
            return BooleanValue.createFalse();
        }

        for (int i = 0; i < right.iteratorSize(); i++)
        {
            ArrayValue entry = (ArrayValue) right.iterate(i);
            if (!value.equals(entry))
            {
                return BooleanValue.createFalse();
            }
        }

        return BooleanValue.createTrue();
    }
}
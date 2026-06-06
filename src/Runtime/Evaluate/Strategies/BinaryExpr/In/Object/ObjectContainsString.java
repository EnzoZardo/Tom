package Runtime.Evaluate.Strategies.BinaryExpr.In.Object;

import Runtime.Values.BooleanValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

public class ObjectContainsString extends ObjectContains<StringValue>
{
    public ObjectContainsString(ObjectValue right, StringValue left)
    {
        super(right, left);
    }

    @Override
    public BooleanValue has()
    {
        StringValue value = (StringValue) left;
        return BooleanValue.create(
            right.properties.keySet().stream().anyMatch(value.value::equals)
        );
    }
}

package Runtime.Evaluate.Strategies.BinaryExpr.In.String;

import Runtime.Values.BooleanValue;
import Runtime.Values.StringValue;

public class StringContainsString extends StringContains<StringValue>
{
    public StringContainsString(StringValue right, StringValue left)
    {
        super(right, left);
    }

    @Override
    public BooleanValue has()
    {
        StringValue string = (StringValue) left;
        return BooleanValue.create(right.value.contains(string.value));
    }
}


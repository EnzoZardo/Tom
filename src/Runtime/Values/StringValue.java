package Runtime.Values;

import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

public class StringValue extends RuntimeValue
{
    public final String value;

    protected StringValue(String value)
    {
        super(ValueType.String);
        this.value = value;
    }

    public static StringValue create(String text)
    {
        return new StringValue(text);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "text: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        if (that.type != type) {
            return false;
        }

        StringValue stringValue = (StringValue) that;

        return stringValue.value.equals(value);
    }

    @Override
    public boolean not()
    {
        return value.isEmpty() || value.isBlank();
    }

    @Override
    public boolean bool()
    {
        return !value.isEmpty() && !value.isBlank();
    }

    @Override
    public String toString()
    {
        return value;
    }
}

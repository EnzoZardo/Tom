package Runtime.Values;

import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

public class StringValue extends RuntimeValue
{
    public final String text;

    protected StringValue(String text)
    {
        super(ValueType.String);
        this.text = text;
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
                "\t".repeat(next) + "text: " + text + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        return text;
    }
}

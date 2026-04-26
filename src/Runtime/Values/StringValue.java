package Runtime.Values;

import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.InvalidIndexException;

import java.util.List;
import java.util.Map;

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

    public static StringValue create(char text)
    {
        return new StringValue(Character.toString(text));
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
    public boolean bool()
    {
        return !value.isEmpty() && !value.isBlank();
    }

    @Override
    public RuntimeValue iterate(int index)
    {
        if (index < 0 || index >= value.length())
        {
            throw new InvalidIndexException("O índice " + index + " é inválido.");
        }

        return StringValue.create(value.charAt(index));
    }

    @Override
    public int iteratorSize()
    {
        return value.length() + 1;
    }

    @Override
    public String toString()
    {
        return value;
    }
}

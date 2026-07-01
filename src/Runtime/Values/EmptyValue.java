package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;

public class EmptyValue extends RuntimeValue
{
    public final String value = ReservedKeys.Empty;

    protected EmptyValue()
    {
        super(ValueType.Empty);
    }

    public static EmptyValue create()
    {
        return new EmptyValue();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        return type == that.type;
    }

    @Override
    public boolean bool()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return ReservedKeys.Empty;
    }

    @Override
    public RuntimeValue copy()
    {
        return new NullValue();
    }
}

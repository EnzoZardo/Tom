package Runtime.Values;

import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

public class NullValue extends RuntimeValue
{
    public final String value = ReservedKeys.Null;

    protected NullValue()
    {
        super(ValueType.Null);
    }

    public static NullValue create()
    {
        return new NullValue();
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
        return ReservedKeys.Null;
    }
}

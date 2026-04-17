package Runtime.Values;

import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

public class BooleanValue extends RuntimeValue
{
    public boolean value;

    protected BooleanValue(boolean value)
    {
        super(ValueType.Boolean);
        this.value = value;
    }

    public static BooleanValue create(boolean value)
    {
        return new BooleanValue(value);
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
    public String toString()
    {
        return value ? ReservedKeys.True : ReservedKeys.False;
    }
}

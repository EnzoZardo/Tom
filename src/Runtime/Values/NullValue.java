package Runtime.Values;

import Constants.ReservedKeys;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

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
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "value: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }
}

package Runtime.Values;

import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

public class BooleanValue extends RuntimeValue
{
    public Boolean value;

    protected BooleanValue(Boolean value)
    {
        super(ValueType.Boolean);
        this.value = value;
    }

    public static BooleanValue create(Boolean value)
    {
        return new BooleanValue(value);
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

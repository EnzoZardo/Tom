package Runtime.Values;

import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

public class NumberValue extends RuntimeValue
{
    public Number number;

    protected NumberValue(Number number)
    {
        super(ValueType.Number);
        this.number = number;
    }

    public static NumberValue create(Number number)
    {
        return new NumberValue(number);
    }

    @Override
    public String print(int level) {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "number: " + number + ",\n" +
                "\t".repeat(level) + "}";
    }
}

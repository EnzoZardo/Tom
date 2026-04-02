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
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "number: " + number + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        return number.toString();
    }
}

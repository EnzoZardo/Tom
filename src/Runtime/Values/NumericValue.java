package Runtime.Values;

import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

public class NumericValue extends RuntimeValue
{
    public float number;
    public boolean isInteger;

    protected NumericValue(float number, boolean isInteger)
    {
        super(ValueType.Numeric);
        this.number = number;
        this.isInteger = isInteger;
    }

    public static NumericValue create(float number, boolean isInteger)
    {
        return new NumericValue(number, isInteger);
    }

    @Override
    public String print(int level) {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "number: " + number + ",\n" +
                "\t".repeat(next) + "isInteger: " + isInteger + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        if (isInteger) {
            return String.valueOf((int) number);
        }
        return String.valueOf(number);
    }
}

package Runtime.Values;

import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

public class NumericValue extends RuntimeValue
{
    public float value;
    public boolean isInteger;

    protected NumericValue(float value, boolean isInteger)
    {
        super(ValueType.Numeric);
        this.value = value;
        this.isInteger = isInteger;
    }

    public static NumericValue create(float number, boolean isInteger)
    {
        return new NumericValue(number, isInteger);
    }

    public NumericValue opposite()
    {
        value = -value;
        return this;
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        if (that.type != type) {
            return false;
        }

        NumericValue numericValue = (NumericValue) that;

        return numericValue.value == value && numericValue.isInteger == isInteger;
    }

    @Override
    public boolean not()
    {
        return value == 0;
    }

    @Override
    public boolean bool()
    {
        return value != 0;
    }

    @Override
    public String print(int level) {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "node: " + type.toString() + ",\n" +
            "\t".repeat(next) + "value: " + value + ",\n" +
            "\t".repeat(next) + "isInteger: " + isInteger + ",\n" +
            "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        if (isInteger) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

}

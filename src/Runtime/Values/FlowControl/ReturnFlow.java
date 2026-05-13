package Runtime.Values.FlowControl;

import Entities.Abstractions.Runtime.RuntimeException;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;

public class ReturnFlow extends RuntimeException
{
    public RuntimeValue value;

    protected ReturnFlow(RuntimeValue value)
    {
        super(ValueType.Return);
        this.value = value;
    }

    public static ReturnFlow create(RuntimeValue value)
    {
        return new ReturnFlow(value);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        return value.equals(that);
    }

    @Override
    public boolean bool()
    {
        return value.bool();
    }
}

package Runtime.Values;

import Entities.Metadata.ParameterMetadata;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

import java.util.function.Function;

public class NativeFunctionValue extends RuntimeValue
{
    public Function<ParameterMetadata, RuntimeValue> call;

    protected NativeFunctionValue(Function<ParameterMetadata, RuntimeValue> call)
    {
        super(ValueType.NativeFunction);
        this.call = call;
    }

    public static NativeFunctionValue create(Function<ParameterMetadata, RuntimeValue> call)
    {
        return new NativeFunctionValue(call);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }
}
package Runtime.Values;

import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

import java.util.ArrayList;
import Runtime.Environment;
import Types.Pair;

import java.util.function.Function;

public class NativeFunctionValue extends RuntimeValue
{
    public Function<Pair<ArrayList<RuntimeValue>, Environment>, RuntimeValue> call;

    protected NativeFunctionValue(Function<Pair<ArrayList<RuntimeValue>, Environment>, RuntimeValue> call)
    {
        super(ValueType.NativeFunction);
        this.call = call;
    }

    public static NativeFunctionValue create(Function<Pair<ArrayList<RuntimeValue>, Environment>, RuntimeValue> call)
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
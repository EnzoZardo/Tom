package Entities.Metadata;

import Entities.Common.Pair;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Environment;

import java.util.ArrayList;
import java.util.List;

public class ParameterMetadata extends Pair<List<RuntimeValue>, Environment>
{
    protected ParameterMetadata(List<RuntimeValue> values, Environment expr)
    {
        super(values, expr);
    }

    public static ParameterMetadata create(ArrayList<RuntimeValue> values, Environment expr)
    {
        return new ParameterMetadata(values.stream().toList(), expr);
    }

    public List<RuntimeValue> getValues()
    {
        return get0();
    }

    public Environment getEnvironment()
    {
        return get1();
    }
}

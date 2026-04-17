package Entities.Metadata;

import Entities.Common.Pair;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Environment;

import java.util.ArrayList;

public class ParameterMetadata extends Pair<ArrayList<RuntimeValue>, Environment>
{
    protected ParameterMetadata(ArrayList<RuntimeValue> type, Environment expr)
    {
        super(type, expr);
    }

    public static ParameterMetadata create(ArrayList<RuntimeValue> type, Environment expr)
    {
        return new ParameterMetadata(type, expr);
    }

    public ArrayList<RuntimeValue> getValues()
    {
        return get0();
    }

    public Environment getEnvironment()
    {
        return get1();
    }
}

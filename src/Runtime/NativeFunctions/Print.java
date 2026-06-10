package Runtime.NativeFunctions;

import Entities.Metadata.ParameterMetadata;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.NullValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Print
{
    public static NullValue call(ParameterMetadata args) {
        List<RuntimeValue> values = args.getValues();
        for (int i = 0; i < values.size(); i++)
        {
            IO.print(values.get(i).toString());
            if (i < values.size() - 1)
            {
                IO.print(' ');
            }
        }
        IO.println();
        return NullValue.create();
    }
}

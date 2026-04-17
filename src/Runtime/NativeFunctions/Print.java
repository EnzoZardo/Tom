package Runtime.NativeFunctions;

import Entities.Metadata.ParameterMetadata;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.NullValue;

public class Print
{
    public static NullValue call(ParameterMetadata args) {
        for (RuntimeValue arg : args.getValues())
        {
            IO.print(arg.toString() + ' ');
        }
        IO.println();
        return NullValue.create();
    }
}

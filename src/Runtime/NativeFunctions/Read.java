package Runtime.NativeFunctions;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.StringValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Read
{
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static StringValue call(ParameterMetadata args) throws IOException
    {
        for (RuntimeValue arg : args.getValues())
        {
            IO.print(arg.toString() + ' ');
        }

        try
        {
            return StringValue.create(reader.readLine());
        }
        catch (IOException _)
        {
            return StringValue.create("");
        }
    }
}

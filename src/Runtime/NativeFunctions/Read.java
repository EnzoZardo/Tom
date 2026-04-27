package Runtime.NativeFunctions;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.StringValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Read
{
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static StringValue call(ParameterMetadata args) throws IOException
    {
        ArrayList<RuntimeValue> values = args.getValues();
        for (int i = 0; i < values.size(); i++)
        {
            IO.print(values.get(i).toString());
            if (i < values.size() - 1)
            {
                IO.print(' ');
            }
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

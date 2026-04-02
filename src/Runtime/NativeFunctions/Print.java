package Runtime.NativeFunctions;

import Runtime.Types.RuntimeValue;
import Runtime.Values.NullValue;
import Types.Pair;
import Runtime.Environment;

import java.util.ArrayList;

public class Print
{
    public static NullValue call(Pair<ArrayList<RuntimeValue>, Environment>  args) {
        for (RuntimeValue arg : args.get0())
        {
            IO.print(arg.toString() + ' ');
        }
        IO.println();
        return NullValue.create();
    }
}

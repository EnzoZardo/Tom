package Runtime.NativeObjects;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Runtime.NativeFunctions.Convert.ToInteger;
import Runtime.NativeFunctions.Convert.ToString;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

import java.util.HashMap;

public class StringObject  extends ObjectValue
{
    protected StringObject(HashMap<String, RuntimeValue> properties)
    {
        super(properties);
    }

    public static ObjectValue create() {
        HashMap<String, RuntimeValue> properties = new HashMap<>() {{
            put("vazio", StringValue.create(""));
            put("espaco", StringValue.create(" "));
            put(ReservedKeys.Convert, NativeFunctionValue.create(ToString::call));
        }};

        return ObjectValue.create(properties);
    }
}

package Runtime.NativeObjects;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Runtime.NativeFunctions.Convert.ToInteger;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;

import java.util.HashMap;

public class IntegerObject extends ObjectValue
{
    private static final String TEN = "dez";
    private static final String ONE = "um";
    private static final String ZERO = "zero";

    protected IntegerObject(HashMap<String, RuntimeValue> properties)
    {
        super(properties);
    }

    public static ObjectValue create() {
        HashMap<String, RuntimeValue> properties = new HashMap<>() {{
            put(ZERO, NumericValue.create(0, true));
            put(ONE, NumericValue.create(1, true));
            put(TEN, NumericValue.create(10, true));
            put(ReservedKeys.Convert, NativeFunctionValue.create(ToInteger::call));
        }};

        return ObjectValue.create(properties);
    }
}

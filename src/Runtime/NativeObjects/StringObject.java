package Runtime.NativeObjects;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Runtime.NativeFunctions.Convert.ToString;
import Runtime.NativeFunctions.Validations.IsEmptyOrSpace;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

import java.util.HashMap;

public class StringObject  extends ObjectValue
{
    private static final String EMPTY = "vazio";
    private static final String SPACE = "espaco";
    private static final String EMPTY_OR_SPACE = "vazioOuEspaco";
    public static final String EMPTY_VALUE = "";
    public static final String SPACE_VALUE = " ";

    protected StringObject(HashMap<String, RuntimeValue> properties)
    {
        super(properties);
    }

    public static ObjectValue create() {
        HashMap<String, RuntimeValue> properties = new HashMap<>() {{
            put(EMPTY, StringValue.create(EMPTY_VALUE));
            put(SPACE, StringValue.create(SPACE_VALUE));
            put(EMPTY_OR_SPACE, NativeFunctionValue.create(IsEmptyOrSpace::call));
            put(ReservedKeys.Convert, NativeFunctionValue.create(ToString::call));
        }};

        return ObjectValue.create(properties);
    }
}

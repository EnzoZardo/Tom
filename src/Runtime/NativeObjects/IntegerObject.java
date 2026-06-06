package Runtime.NativeObjects;

import Ast.Types.ObjectType;
import Ast.Types.ObjectTypeProperty;
import Ast.Types.Primitive.IntegerType;
import Ast.Types.Primitive.NativeFunctionType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Runtime.NativeFunctions.Convert.ToInteger;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class IntegerObject extends ObjectValue
{
    private static final String TEN = "dez";
    private static final String ONE = "um";
    private static final String ZERO = "zero";

    private static final HashMap<String, RuntimeValue> properties = new HashMap<>() {{
        put(ZERO, NumericValue.createInteger(0));
        put(ONE, NumericValue.createInteger(1));
        put(TEN, NumericValue.createInteger(10));
        put(ReservedKeys.Convert, NativeFunctionValue.create(ToInteger::call));
    }};

    private static final ArrayList<ObjectTypeProperty> propertiesTypes = new ArrayList<>() {{
        add(ObjectTypeProperty.create(ZERO, IntegerType.create()));
        add(ObjectTypeProperty.create(ONE, IntegerType.create()));
        add(ObjectTypeProperty.create(TEN, IntegerType.create()));
        add(ObjectTypeProperty.create(ReservedKeys.Convert, NativeFunctionType.create(IntegerType.create())));
    }};

    protected IntegerObject(HashMap<String, RuntimeValue> properties)
    {
        super(properties, true);
    }

    public static ObjectType type()
    {
        return ObjectType.create(propertiesTypes);
    }

    public static ObjectValue create()
    {
        return ObjectValue.createFreeze(properties);
    }
}

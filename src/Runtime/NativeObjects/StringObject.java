package Runtime.NativeObjects;

import Ast.Types.ObjectType;
import Ast.Types.ObjectTypeProperty;
import Ast.Types.Primitive.BooleanType;
import Ast.Types.Primitive.NativeFunctionType;
import Ast.Types.Primitive.StringType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Runtime.NativeFunctions.Convert.ToString;
import Runtime.NativeFunctions.Validations.IsEmptyOrSpace;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

import java.util.ArrayList;
import java.util.HashMap;

public class StringObject extends ObjectValue
{
    private static final String EMPTY = "vazio";
    private static final String SPACE = "espaco";
    private static final String EMPTY_OR_SPACE = "vazioOuEspaco";
    public static final String EMPTY_VALUE = "";
    public static final String SPACE_VALUE = " ";

    private static final ArrayList<ObjectTypeProperty> propertiesTypes = new ArrayList<>() {{
        add(ObjectTypeProperty.create(EMPTY, StringType.create()));
        add(ObjectTypeProperty.create(SPACE, StringType.create()));
        add(ObjectTypeProperty.create(EMPTY_OR_SPACE, NativeFunctionType.create(BooleanType.create())));
        add(ObjectTypeProperty.create(ReservedKeys.Convert, NativeFunctionType.create(StringType.create())));
    }};

    private static final HashMap<String, RuntimeValue> properties = new HashMap<>() {{
        put(EMPTY, StringValue.create(EMPTY_VALUE));
        put(SPACE, StringValue.create(SPACE_VALUE));
        put(EMPTY_OR_SPACE, NativeFunctionValue.create(IsEmptyOrSpace::call));
        put(ReservedKeys.Convert, NativeFunctionValue.create(ToString::call));
    }};

    protected StringObject(HashMap<String, RuntimeValue> properties)
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

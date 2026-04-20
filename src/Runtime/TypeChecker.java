package Runtime;

import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedPrimitiveTypes;
import Entities.Enums.TypeKind;
import Ast.Types.*;
import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.ArrayValue;
import Runtime.Values.FunctionValue;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;

public class TypeChecker
{
    protected TypeChecker() {}

    public static boolean check(Environment env, RuntimeValue value, Type expected)
    {
        return switch (expected.type)
        {
            case TypeKind.SymbolType ->
            {
                SymbolType symbol = (SymbolType) expected;
                yield checkSymbol(env, symbol, value);
            }
            case TypeKind.ObjectType ->
            {
                ObjectType object = (ObjectType) expected;
                yield checkObject(env, object, value);
            }
            case TypeKind.ArrayType ->
            {
                ArrayType array = (ArrayType) expected;
                yield checkArray(env, array, value);
            }
            case TypeKind.FunctionType ->
            {
                FunctionType function = (FunctionType) expected;
                yield checkFunction(env, function, value);
            }
            default -> false;
        };
    }

    private static boolean checkSymbol(Environment env, SymbolType symbol, RuntimeValue value)
    {
        if (ReservedPrimitiveTypes.isReserved(symbol.value))
        {
            return checkPrimitive(symbol.value, value);
        }

        Environment environment = env.resolveType(symbol.value);
        Type type = environment.lookupType(symbol.value);
        return check(env, value, type);
    }

    private static boolean checkPrimitive(String symbol, RuntimeValue value)
    {
        return switch (symbol)
        {
            case ReservedKeys.Integer -> value.type == ValueType.Numeric && ((NumericValue) value).isInteger;
            case ReservedKeys.Float -> value.type == ValueType.Numeric && !((NumericValue) value).isInteger;
            case ReservedKeys.Boolean -> value.type == ValueType.Boolean;
            case ReservedKeys.String -> value.type == ValueType.String;
            case ReservedKeys.Object -> value.type == ValueType.Object;
            case ReservedKeys.Null -> value.type == ValueType.Null;
            default -> false;
        };
    }

    private static boolean checkFunction(Environment env, FunctionType type, RuntimeValue value)
    {
        if (value.type != ValueType.Function)
        {
            return false;
        }

        FunctionValue function = (FunctionValue) value;

        if (function.parameters.size() != type.parameters.size())
        {
            return false;
        }

        if (function.returnType.type != type.returnType.type) {
            return false;
        }

        Type currentReturn = Type.reduce(env, function.returnType);
        Type expectedReturn = Type.reduce(env, type.returnType);

        if (Type.equals(currentReturn, expectedReturn).isFailure())
        {
            return false;
        }

        for (int i = 0; i < type.parameters.size(); i++)
        {
            Type currentType = Type.reduce(env, function.parameters.get(i).getType());
            Type expectedType = Type.reduce(env,type.parameters.get(i));
            if (Type.equals(currentType, expectedType).isSuccess())
            {
                continue;
            }

            return false;
        }

        return true;
    }

    private static boolean checkArray(Environment env, ArrayType type, RuntimeValue value)
    {
        if (value.type != ValueType.Array)
        {
            return false;
        }

        ArrayValue array = (ArrayValue) value;

        if (array.items.isEmpty())
        {
            return true;
        }

        for (RuntimeValue item : array.items.values())
        {
            if (check(env, item, type.underlying)) {
                continue;
            }

            return false;
        }

        return true;
    }

    private static boolean checkObject(Environment env, ObjectType type, RuntimeValue value)
    {
        if (value.type != ValueType.Object)
        {
            return false;
        }

        ObjectValue object = (ObjectValue) value;

        if (object.properties.size() != type.properties.size())
        {
            return false;
        }

        for (ObjectTypeProperty prop : type.properties)
        {
            if (object.properties.containsKey(prop.key))
            {
                RuntimeValue property = object.properties.get(prop.key);

                if (check(env, property, prop.type))
                {
                    continue;
                }
            }

            return false;
        }

        return true;
    }
}

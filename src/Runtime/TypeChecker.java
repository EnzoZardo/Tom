package Runtime;

import Ast.Enums.TypeKind;
import Ast.Statements.Types.*;
import Constants.ReservedKeys;
import Constants.ReservedPrimitiveTypes;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;
import Runtime.Values.FunctionValue;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;

public class TypeChecker
{
    protected TypeChecker() {}

    public static TypeChecker create()
    {
        return new TypeChecker();
    }

    public static boolean check(Environment env, RuntimeValue value, Type expected)
    {
        switch (expected.type) {
            case TypeKind.SymbolType:
                SymbolType symbol = (SymbolType) expected;
                return checkSymbol(env, symbol, value);
            case TypeKind.ObjectType:
                ObjectType object = (ObjectType) expected;
                return checkObject(env, object, value);
            case TypeKind.FunctionType:
                FunctionType function = (FunctionType) expected;
                return checkFunction(env, function, value);
            case TypeKind.ArrayType:
            default: return false;
        }
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
            case ReservedKeys.String -> value.type == ValueType.String;
            case ReservedKeys.Boolean -> value.type == ValueType.Boolean;
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

        for (int i = 0; i < type.parameters.size(); i++)
        {
            //TODO: here, we have to create an equals method for every type
            if (function.parameters.get(i).getType().type == type.parameters.get(i).type)
            {
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

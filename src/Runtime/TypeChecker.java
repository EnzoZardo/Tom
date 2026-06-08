package Runtime;

import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedPrimitiveTypes;
import Entities.Enums.TypeKind;
import Ast.Types.*;
import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.ArrayValue;
import Runtime.Values.ClassValue;
import Runtime.Values.FunctionValue;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;

public class TypeChecker
{
    protected TypeChecker() {}

    public static ErrorOr<Void> check(Environment env, RuntimeValue value, Type expected)
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
            case TypeKind.ClassType ->
            {
                ClassType classType = (ClassType) expected;
                yield checkClass(classType, value);
            }
            default -> ErrorOr.Fail("Tipo informado é desconhecido.");
        };
    }

    private static ErrorOr<Void> checkSymbol(Environment env, SymbolType symbol, RuntimeValue value)
    {
        if (ReservedPrimitiveTypes.isReserved(symbol.value))
        {
            return checkPrimitive(symbol.value, value);
        }

        Environment environment = env.resolveType(symbol.value);
        Type type = environment.lookupType(symbol.value);
        return check(env, value, type);
    }

    //TODO: insert checks on each primitive type
    private static ErrorOr<Void> checkPrimitive(String symbol, RuntimeValue value)
    {
        return switch (symbol)
        {
            case ReservedKeys.Integer ->
            {
                if (value.type == ValueType.Numeric && ((NumericValue) value).isInteger)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um inteiro válido.", value));
            }
            case ReservedKeys.Float ->
            {
                if (value.type == ValueType.Numeric && !((NumericValue) value).isInteger)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um real válido.", value));
            }
            case ReservedKeys.Boolean ->
            {
                if (value.type == ValueType.Boolean)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um lógico válido.", value));
            }
            case ReservedKeys.String ->
            {
                if (value.type == ValueType.String)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um texto válido.", value));
            }
            case ReservedKeys.Object ->
            {
                if (value.type == ValueType.Object)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um objeto válido.", value));
            }
            case ReservedKeys.Null -> {
                if (value.type == ValueType.Null)
                {
                    yield ErrorOr.Success();
                }
                yield ErrorOr.Fail(String.format("O valor '%s' informado não é um nulo válido.", value));
            }
            default -> ErrorOr.Fail("Tipo " + symbol + " desconhecido.");
        };
    }

    private static ErrorOr<Void> checkFunction(Environment env, FunctionType type, RuntimeValue value)
    {
        if (value.type != ValueType.Function)
        {
            return ErrorOr.Fail("O valor informado não é uma função.");
        }

        FunctionValue function = (FunctionValue) value;

        if (function.parameters.size() != type.parameters.size())
        {
            return ErrorOr.Fail("O número de parâmetros informados está incorreto.");
        }

        Type currentReturn = Type.reduce(env, function.returnType);
        Type expectedReturn = Type.reduce(env, type.returnType);
        ErrorOr<Void> equality = Type.equals(currentReturn, expectedReturn);

        if (equality.isError())
        {
            return equality;
        }

        for (int i = 0; i < type.parameters.size(); i++)
        {
            Type currentType = Type.reduce(env, function.parameters.get(i).getType());
            Type expectedType = Type.reduce(env,type.parameters.get(i));
            equality = Type.equals(currentType, expectedType);

            if (equality.isError())
            {
                return equality;
            }
        }

        return ErrorOr.Success();
    }

    private static ErrorOr<Void> checkClass(ClassType type, RuntimeValue value)
    {
        if (value.type != ValueType.Class)
        {
            return ErrorOr.Fail("O valor informado não é uma classe.");
        }

        ClassValue classValue = (ClassValue) value;

        if (!classValue.className.equals(type.name))
        {
            return ErrorOr.Fail("Classes diferentes.");
        }

        return ErrorOr.Success();
    }


    private static ErrorOr<Void> checkArray(Environment env, ArrayType type, RuntimeValue value)
    {
        if (value.type != ValueType.Array)
        {
            return ErrorOr.Fail("O valor informado não é uma lista.");
        }

        ArrayValue array = (ArrayValue) value;

        if (array.items.isEmpty())
        {
            return ErrorOr.Success();
        }

        for (RuntimeValue item : array.items.values())
        {
            ErrorOr<Void> equality = check(env, item, type.underlying);

            if (equality.isError())
            {
                return equality;
            }
        }

        return ErrorOr.Success();
    }

    private static ErrorOr<Void> checkObject(Environment env, ObjectType type, RuntimeValue value)
    {
        if (value.type != ValueType.Object)
        {
            return ErrorOr.Fail("O valor informado não é um objeto.");
        }

        ObjectValue object = (ObjectValue) value;

        for (ObjectTypeProperty prop : type.properties)
        {
            if (!object.properties.containsKey(prop.key))
            {
                return ErrorOr.Fail("A propriedade '" + prop.key + "' não foi informada.");
            }

            RuntimeValue property = object.properties.get(prop.key);

            ErrorOr<Void> equality = check(env, property, prop.type);

            if (equality.isError())
            {
                return equality;
            }
        }

        return ErrorOr.Success();
    }
}

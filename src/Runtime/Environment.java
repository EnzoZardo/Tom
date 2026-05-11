package Runtime;

import Ast.Types.ArrayType;
import Ast.Types.ObjectType;
import Ast.Types.Primitive.*;
import Ast.Types.SymbolType;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Enums.TypeKind;
import Entities.Exceptions.*;
import Entities.Exceptions.Evaluate.InvalidAssignmentExpression;
import Entities.Exceptions.Evaluate.InvalidMemberAssignException;
import Runtime.NativeFunctions.Interval;
import Runtime.NativeFunctions.Print;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.NativeFunctions.Read;
import Runtime.NativeObjects.IntegerObject;
import Runtime.NativeObjects.StringObject;
import Runtime.Values.*;
import Entities.Metadata.ValueMetadata;

import java.io.IOException;
import java.util.HashMap;

public class Environment
{
    private final Environment parent;
    private final HashMap<String, ValueMetadata> variables;
    private final HashMap<String, ValueMetadata> constants;
    private final HashMap<String, Type> types;

    private Environment() throws AlreadyDeclaredVariableException
    {
        parent = null;
        types = new HashMap<>();
        variables = new HashMap<>();
        constants = new HashMap<>();
        setupScope();
    }

    private Environment(Environment parent)
    {
        this.parent = parent;
        types = new HashMap<>();
        variables = new HashMap<>();
        constants = new HashMap<>();
    }

    public static Environment create() throws AlreadyDeclaredVariableException
    {
        return new Environment();
    }

    public static Environment create(Environment parentEnv) throws AlreadyDeclaredVariableException
    {
        return new Environment(parentEnv);
    }

    public RuntimeValue declareVariable(String name, RuntimeValue value, Type type, boolean constant) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        if (constant)
        {
            constants.put(name, ValueMetadata.create(type, value));
            return value;
        }

        variables.put(name, ValueMetadata.create(type, value));
        return value;
    }

    public RuntimeValue declareConstant(String name, RuntimeValue value, Type type) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        constants.put(name, ValueMetadata.create(type, value));
        return value;
    }

    public RuntimeValue assignVariable(String name, RuntimeValue value)
    {
        Environment variableEnvironment = resolve(name);

        if (variableEnvironment.constants.containsKey(name))
        {
            throw new ConstantAssignmentException(String.format(
                "Não podemos atribuir a variável '%s', ela é constante.",
                name));
        }

        ValueMetadata variable = variableEnvironment.variables.get(name);

        Type expectedType = variable.getType();
        ErrorOr<Void> equality = TypeChecker.check(this, value, expectedType);
        if (equality.isError())
        {
            throw new ExpectedTypeNotMatch(equality.error.getMessage());
        }

        variableEnvironment.variables.put(name, ValueMetadata.create(variable.getType(), value));
        return value;
    }

    public RuntimeValue assignIndex(String name, int number, RuntimeValue value)
    {
        Environment variableEnvironment = resolve(name);
        ValueMetadata obj = variableEnvironment.constants.get(name);

        if (variableEnvironment.variables.containsKey(name))
        {
            obj = variableEnvironment.variables.get(name);
        }

        if (obj.getValue().type != ValueType.Array)
        {
            throw new InvalidMemberAssignException("O valor para o qual está tentando dar um novo " +
                    "valor não é do tipo lista.");
        }

        ArrayValue arrayValue = (ArrayValue) obj.getValue();
        Type reducedType = Type.reduce(this, obj.getType());

        if (arrayValue.isFrozen())
        {
            throw new InvalidAssignmentExpression("Vocẽ não pode alterar a chave de um valor congelado.");
        }

        if (reducedType.type == TypeKind.ArrayType && arrayValue.items.containsKey(number))
        {
            arrayValue.items.put(number, value);
            return arrayValue;
        }

        throw new InvalidMemberAssignException("Não foi encontrada nenhum índice com o número " +
                number + " para esta.");
    }

    public RuntimeValue assignMember(String name, String keyName, RuntimeValue value)
    {
        Environment variableEnvironment = resolve(name);
        ValueMetadata obj = variableEnvironment.constants.get(name);

        if (variableEnvironment.variables.containsKey(name))
        {
            obj = variableEnvironment.variables.get(name);
        }

        if (obj.getValue().type != ValueType.Object)
        {
            throw new InvalidMemberAssignException("O valor para o qual está tentando dar um novo " +
                    "valor não é do tipo objeto.");
        }

        ObjectValue objectValue = (ObjectValue) obj.getValue();
        Type reducedType = Type.reduce(this, obj.getType());

        if (objectValue.isFrozen())
        {
            throw new InvalidAssignmentExpression("Vocẽ não pode alterar a chave de um valor congelado.");
        }

        if (reducedType.type == TypeKind.ObjectType)
        {
            if (objectValue.properties.containsKey(keyName))
            {
                objectValue.properties.put(keyName, value);
                return objectValue;
            }

            throw new InvalidMemberAssignException("Não foi encontrada nenhuma chave com o nome " +
                keyName + " para este objeto.");
        }

        if (reducedType.type == TypeKind.SymbolType)
        {
            SymbolType symbol = (SymbolType) obj.getType();

            if (ReservedKeys.Object.equals(symbol.value))
            {
                objectValue.properties.put(keyName, value);
                return objectValue;
            }
        }

        throw new InvalidMemberAssignException("Não foi possível dar valor para a chave " +
                keyName + " para este objeto.");
    }

    public RuntimeValue lookupVariable(String name)
    {
        Environment variableEnvironment = resolve(name);

        if (variableEnvironment.variables.containsKey(name))
        {
            return variableEnvironment.variables.get(name).getValue();
        }

        return variableEnvironment.constants.get(name).getValue();
    }

    public Type declareType(String name, Type type)
    {
        if (types.containsKey(name))
        {
            throw new TypeReassignmentException(String.format(
                "Não podemos atribuir o tipo %s. Ele já existe.",
                name));
        }

        return types.put(name, type);
    }

    public Type lookupType(String name)
    {
        Environment typeEnvironment = resolveType(name);

        return typeEnvironment.types.get(name);
    }

    public Environment resolveType(String name)
    {
        if (this.types.containsKey(name))
        {
            return this;
        }

        if (this.parent == null)
        {
            // TODO: change this
            throw new InvalidVariableException(name);
        }

        return this.parent.resolveType(name);
    }

    public Environment resolve(String name)
    {
        if (this.variables.containsKey(name) || this.constants.containsKey(name))
        {
            return this;
        }

        if (this.parent == null)
        {
            throw new InvalidVariableException(name);
        }

        return this.parent.resolve(name);
    }

    private void setupScope() throws AlreadyDeclaredVariableException
    {
        Type nullType = declareType(ReservedKeys.Null, NullType.create());
        Type stringType = declareType(ReservedKeys.String, StringType.create());
        Type boolType = declareType(ReservedKeys.Boolean, BooleanType.create());
        Type intType = declareType(ReservedKeys.Integer, IntegerType.create());

        declareConstant(ReservedKeys.Integer, IntegerObject.create(), IntegerObject.type());
        declareConstant(ReservedKeys.String, StringObject.create(), StringObject.type());

        declareConstant(ReservedKeys.Null, NullValue.create(), nullType);
        declareConstant(ReservedKeys.True, BooleanValue.create(true), boolType);
        declareConstant(ReservedKeys.False, BooleanValue.create(false), boolType);
        declareConstant(ReservedKeys.Print,
            NativeFunctionValue.create(Print::call),
            NativeFunctionType.create(nullType));
        declareConstant(ReservedKeys.Interval, NativeFunctionValue.create(Interval::call),
            NativeFunctionType.create(ArrayType.create(intType)));
        declareConstant(ReservedKeys.Read, NativeFunctionValue.create(x ->
        {
            try
            {
                return Read.call(x);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }), NativeFunctionType.create(stringType));
    }
}

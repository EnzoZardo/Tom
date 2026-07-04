package Runtime;

import Ast.Types.ArrayType;
import Ast.Types.ObjectType;
import Ast.Types.Primitive.*;
import Ast.Types.SymbolType;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Enums.Runtime.ValueType;
import Entities.Enums.TypeKind;
import Entities.Exceptions.*;
import Entities.Exceptions.Evaluate.InvalidAssignmentExpression;
import Entities.Exceptions.Evaluate.InvalidMemberAssignException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Metadata.ParameterMetadata;
import Runtime.NativeFunctions.Interval;
import Runtime.NativeFunctions.Print;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.NativeFunctions.Read;
import Runtime.NativeObjects.IntegerObject;
import Runtime.NativeObjects.StringObject;
import Runtime.Values.*;
import Entities.Metadata.ValueMetadata;
import Runtime.Values.ClassValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Environment
{
    public final ClassValue currentClass;

    private final Environment parent;
    private final Set<String> imports;
    private final HashMap<String, ValueMetadata> variables;
    private final HashMap<String, ValueMetadata> constants;
    private final HashMap<String, Type> types;

    private Environment() throws AlreadyDeclaredVariableException
    {
        parent = null;
        types = new HashMap<>();
        variables = new HashMap<>();
        constants = new HashMap<>();
        imports = new HashSet<>();
        currentClass = null;
        setupScope();
    }

    private Environment(Environment parent, ClassValue classValue)
    {
        this.parent = parent;
        this.currentClass = classValue;
        types = new HashMap<>();
        variables = new HashMap<>();
        constants = new HashMap<>();
        imports = new HashSet<>();
    }

    public static Environment create() throws AlreadyDeclaredVariableException
    {
        return new Environment();
    }

    public static Environment create(Environment parentEnv) throws AlreadyDeclaredVariableException
    {
        return new Environment(parentEnv, null);
    }

    public static Environment create(Environment parentEnv, ClassValue currentClass) throws AlreadyDeclaredVariableException
    {
        return new Environment(parentEnv, currentClass);
    }

    public RuntimeValue declareClass(String name, RuntimeValue classValue, Type classType)
        throws AlreadyDeclaredVariableException
    {
        declareType(name, classType);
        declareConstant(name, classValue, classType);
        return classValue;
    }

    public RuntimeValue declareVariable(
        String name,
        RuntimeValue value,
        Type type,
        boolean constant,
        Function<RuntimeValue, RuntimeValue> mapper) throws AlreadyDeclaredVariableException
    {
        return declareVariable(name, mapper.apply(value), type, constant);
    }

    public RuntimeValue declareVariable(
        String name,
        RuntimeValue value,
        Type type,
        boolean constant)
        throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
            throw new AlreadyDeclaredVariableException(name);

        if (constant)
        {
            constants.put(name, ValueMetadata.create(type, value));
            return value;
        }

        variables.put(name, ValueMetadata.create(type, value));
        return value;
    }

    public RuntimeValue declareConstant(
        String name,
        RuntimeValue value,
        Type type,
        Function<RuntimeValue, RuntimeValue> mapper) throws AlreadyDeclaredVariableException
    {
        return declareConstant(name, mapper.apply(value), type);
    }

    public RuntimeValue declareConstant(String name, RuntimeValue value, Type type)
        throws AlreadyDeclaredVariableException
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
            throw new ConstantAssignmentException(String.format(
                "Não podemos atribuir a variável '%s', ela é constante.",
                name));

        ValueMetadata variable = variableEnvironment.variables.get(name);

        Type expectedType = variable.getType();
        ErrorOr<Void> equality = TypeChecker.check(this, value, expectedType);
        if (equality.isError())
            throw new ExpectedTypeNotMatch(equality.error.getMessage());

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

    public RuntimeValue assignClassMember(String name, String keyName, RuntimeValue value, ClassValue caller)
    {
        Environment variableEnvironment = resolve(name);
        ValueMetadata valueMetadata = variableEnvironment.constants.get(name);

        if (variableEnvironment.variables.containsKey(name))
        {
            valueMetadata = variableEnvironment.variables.get(name);
        }

        if (valueMetadata.getValue().type != ValueType.Class)
        {
            throw new InvalidMemberAssignException("O valor para o qual está tentando dar um novo " +
                "valor não é do tipo classe.");
        }

        ClassValue classValue = (ClassValue) valueMetadata.getValue();

        if (!classValue.members.containsKey(keyName)
                && (classValue.parent == null || !classValue.parent.members.containsKey(keyName)))
            throw new InvalidMemberAssignException("Não foi encontrada nenhuma chave com o nome " +
                    keyName + " para este objeto.");

        ClassMemberValue member = classValue.members.get(keyName);
        if (member == null) member = classValue.parent.members.get(keyName);

        ErrorOr<Void> accessResult = AccessChecker.canAccess(member, caller, keyName);

        if (accessResult.isError()) throw new InvalidMemberAssignException(accessResult.error.getMessage());

        Type expectedType = member.type;
        ErrorOr<Void> equality = TypeChecker.check(this, value, expectedType);
        if (equality.isError()) throw new ExpectedTypeNotMatch(equality.error.getMessage());

        member.value = value;
        classValue.members.put(keyName, member);
        return classValue;

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

    public boolean canImport(String path)
    {
        if (this.imports.contains(path))
        {
            return false;
        }

        if (this.parent == null)
        {
            return true;
        }

        return this.parent.canImport(path);
    }

    public void addImport(String path)
    {
        this.imports.add(path);
    }

    private void setupScope() throws AlreadyDeclaredVariableException
    {
        Type stringType = declareType(ReservedKeys.String, StringType.create());
        Type boolType = declareType(ReservedKeys.Boolean, BooleanType.create());
        Type intType = declareType(ReservedKeys.Integer, IntegerType.create());
        Type emptyType = declareType(ReservedKeys.Empty, EmptyType.create());
        Type nullType = declareType(ReservedKeys.Null, NullType.create());

        declareConstant(ReservedKeys.Integer, IntegerObject.create(), IntegerObject.type());
        declareConstant(ReservedKeys.String, StringObject.create(), StringObject.type());
        declareConstant(ReservedKeys.False, BooleanValue.create(false), boolType);
        declareConstant(ReservedKeys.True, BooleanValue.create(true), boolType);
        declareConstant(ReservedKeys.Empty, EmptyValue.create(), emptyType);
        declareConstant(ReservedKeys.Null, NullValue.create(), nullType);

        declareConstant(ReservedKeys.Print,
            NativeFunctionValue.create(Print::call),
            NativeFunctionType.create(nullType));

        declareConstant(ReservedKeys.Interval,
            NativeFunctionValue.create(Interval::call),
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

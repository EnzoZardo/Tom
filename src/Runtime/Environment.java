package Runtime;

import Ast.Statements.Types.Primitive.*;
import Ast.Statements.Types.SymbolType;
import Ast.Statements.Types.Type;
import Constants.ReservedKeys;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.ConstantAssignmentException;
import Exceptions.InvalidVariableException;
import Exceptions.TypeReassignmentException;
import Runtime.NativeFunctions.Print;
import Runtime.Types.RuntimeValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.NullValue;
import Types.ValueMetadata;

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

    public RuntimeValue declareVariable(String name, RuntimeValue value, boolean constant) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        if (constant)
        {
            constants.put(name, ValueMetadata.create(null, value));
            return value;
        }

        variables.put(name, ValueMetadata.create(null, value));
        return value;
    }

    public RuntimeValue declareConstant(String name, RuntimeValue value) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        constants.put(name, ValueMetadata.create(null, value));
        return value;
    }

    public RuntimeValue assignVariable(String name, RuntimeValue value)
    {
        Environment variableEnvironment = resolve(name);

        if (variableEnvironment.constants.containsKey(name))
        {
            throw new ConstantAssignmentException("Cannot assign variable, it was declared as constant.");
        }

        variableEnvironment.variables.put(name, ValueMetadata.create(variableEnvironment.variables.get(name).getType(), value));
        return value;
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

    public void declareType(String name, Type type)
    {
        if (types.containsKey(name))
        {
            throw new TypeReassignmentException("Cannot assign type, it was already declared.");
        }

        types.put(name, type);
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
        declareType(ReservedKeys.Null, NullType.create());
        declareType(ReservedKeys.Float, FloatType.create());
        declareType(ReservedKeys.String, StringType.create());
        declareType(ReservedKeys.Boolean, BooleanType.create());
        declareType(ReservedKeys.Integer, IntegerType.create());

        declareConstant(ReservedKeys.Null, NullValue.create());
        declareConstant(ReservedKeys.True, BooleanValue.create(true));
        declareConstant(ReservedKeys.False, BooleanValue.create(false));
        declareConstant(ReservedKeys.Print, NativeFunctionValue.create(Print::call));
    }
}

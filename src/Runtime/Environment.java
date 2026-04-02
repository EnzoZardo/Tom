package Runtime;

import Constants.ReservedKeys;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.ConstantAssignmentException;
import Exceptions.InvalidVariableException;
import Runtime.NativeFunctions.Print;
import Runtime.Types.RuntimeValue;
import Runtime.Values.BooleanValue;
import Runtime.Values.NativeFunctionValue;
import Runtime.Values.NullValue;
import java.util.HashMap;

public class Environment
{
    private final Environment parent;
    private final HashMap<String, RuntimeValue> variables;
    private final HashMap<String, RuntimeValue> constants;

    private Environment() throws AlreadyDeclaredVariableException
    {
        parent = null;
        variables = new HashMap<>();
        constants = new HashMap<>();
        setupScope();
    }

    private Environment(Environment parent)
    {
        this.parent = parent;
        variables = new HashMap<>();
        constants = new HashMap<>();
    }

    public static Environment create() throws AlreadyDeclaredVariableException
    {
        return new Environment();
    }

    public RuntimeValue declareVariable(String name, RuntimeValue value, boolean constant) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        if (constant)
        {
            constants.put(name, value);
            return value;
        }

        variables.put(name, value);
        return value;
    }

    public void declareConstant(String name, RuntimeValue value) throws AlreadyDeclaredVariableException
    {
        if (variables.containsKey(name) || constants.containsKey(name))
        {
            throw new AlreadyDeclaredVariableException(name);
        }

        constants.put(name, value);
    }

    public RuntimeValue assignVariable(String name, RuntimeValue value)
    {
        Environment variableEnvironment = resolve(name);

        if (variableEnvironment.constants.containsKey(name))
        {
            throw new ConstantAssignmentException("Cannot assign variable, it was declared as constant.");
        }

        variableEnvironment.variables.put(name, value);
        return value;
    }

    public RuntimeValue lookupVariable(String name)
    {
        Environment variableEnvironment = resolve(name);

        if (variableEnvironment.variables.containsKey(name))
        {
            return variableEnvironment.variables.get(name);
        }

        return variableEnvironment.constants.get(name);
    }

    public Environment resolve(String name)
    {
        if (this.variables.containsKey(name)
                || this.constants.containsKey(name))
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
        declareConstant(ReservedKeys.Null, NullValue.create());
        declareConstant(ReservedKeys.True, BooleanValue.create(true));
        declareConstant(ReservedKeys.False, BooleanValue.create(false));
        declareConstant(ReservedKeys.Print, NativeFunctionValue.create(Print::call));
    }
}

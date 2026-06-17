package Runtime.Evaluate.Strategies.CallExpr;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Evaluate.Strategies.CallExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Metadata.ParameterMetadata;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.NativeFunctionValue;

import java.util.ArrayList;

public class NativeFunctionCallStrategy implements CallExprStrategy
{
    @Override
    public RuntimeValue evaluate(CallExpr expr, RuntimeValue caller, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();
        for (Expr arg : expr.arguments) args.add(ClassMemberValue.mapToValue(Interpreter.evaluate(arg, environment)));

        NativeFunctionValue nativeFunction = (NativeFunctionValue) caller;

        ParameterMetadata parameters = ParameterMetadata.create(args, environment);
        return nativeFunction.call.apply(parameters);
    }
}

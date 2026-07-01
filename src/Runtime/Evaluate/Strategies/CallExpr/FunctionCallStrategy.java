package Runtime.Evaluate.Strategies.CallExpr;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Abstractions.Evaluate.Strategies.CallExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Values.EmptyValue;
import Runtime.Values.FlowControl.ReturnFlow;
import Runtime.Values.FunctionValue;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Values.NullValue;
import Runtime.TypeChecker;

import java.util.ArrayList;

public class FunctionCallStrategy implements CallExprStrategy
{
    @Override
    public RuntimeValue evaluate(CallExpr expr, RuntimeValue caller, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr arg : expr.arguments) args.add(Interpreter.evaluate(arg, environment));

        FunctionValue function = (FunctionValue) caller;
        Environment scope = Environment.create(function.declarationEnv);

        if (function.parameters.size() != expr.arguments.size())
            throw new IncorrectNumberOfArgumentsException(String.format(
                "A função %s esperava %d argumento(s), mas recebeu %d.",
                function.name,
                function.parameters.size(),
                expr.arguments.size()));

        for (int i = 0; i < function.parameters.size(); i++)
        {
            ArgumentMetadata param = function.parameters.get(i);
            String name = param.getName();

            ErrorOr<Void> equality = TypeChecker.check(environment, args.get(i), param.getType());
            if (equality.isError())
                throw new RuntimeException(String.format(
                    "Tipo incorreto informado para o argumento '%s'. %s",
                    name,
                    equality.error.getMessage()));

            scope.declareVariable(name, args.get(i), param.getType(), false);
        }

        RuntimeValue result = NullValue.create();
        for (Statement statement : function.body)
        {
            result = Interpreter.evaluate(statement, scope);

            if (result.type == ValueType.Return) break;
        }

        RuntimeValue ret = result.type == ValueType.Return
            ? ((ReturnFlow) result).value
            : EmptyValue.create();

        ErrorOr<Void> equality = TypeChecker.check(environment, ret, function.returnType);

        if (equality.isError())
            throw new ExpectedTypeNotMatch(String.format(
                "Tipo de retorno não condiz com o tipo esperado. %s",
                equality.error.getMessage()));

        return ret;
    }
}


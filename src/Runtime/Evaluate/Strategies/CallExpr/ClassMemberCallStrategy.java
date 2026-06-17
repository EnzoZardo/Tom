package Runtime.Evaluate.Strategies.CallExpr;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Abstractions.Evaluate.Strategies.CallExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.Evaluate.InvalidMemberAssignException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Exceptions.InvalidCallException;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.FlowControl.ReturnFlow;
import Runtime.Values.FunctionValue;
import Runtime.Values.NullValue;
import Runtime.TypeChecker;
import Runtime.AccessChecker;

import java.util.ArrayList;

public class ClassMemberCallStrategy implements CallExprStrategy
{
    @Override
    public RuntimeValue evaluate(CallExpr expr, RuntimeValue caller, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();
        for (Expr arg : expr.arguments) args.add(Interpreter.evaluate(arg, environment));

        ClassMemberValue member = (ClassMemberValue) caller;
        FunctionValue function = (FunctionValue) member.value;

        ErrorOr<Void> accessResult = AccessChecker.canAccess(member, environment.currentClass, function.name);

        if (accessResult.isError()) throw new InvalidCallException(accessResult.error.getMessage());

        Environment scope = Environment.create(function.declarationEnv, member.owner);

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

            ErrorOr<Void> equality = TypeChecker.check(environment, ClassMemberValue.mapToValue(args.get(i)), param.getType());
            if (equality.isError())
                throw new RuntimeException(String.format(
                    "Tipo incorreto informado para o argumento '%s'. %s",
                    name,
                    equality.error.getMessage()));

            RuntimeValue value = args.get(i);

            scope.declareVariable(name, value, param.getType(), false, ClassMemberValue::mapToValue);
        }

        Environment typeEnv = environment.resolveType(member.owner.className);
        Type type = typeEnv.lookupType(member.owner.className);

        if (!member.isStatic)
        {
            if (member.owner.parent != null)
            {
                Environment parentTypeEnv = environment.resolveType(member.owner.parent.className);
                Type parentType = parentTypeEnv.lookupType(member.owner.parent.className);
                scope.declareVariable(ReservedKeys.Super, member.owner.parent, parentType, false);
            }

            scope.declareVariable(ReservedKeys.This, member.owner, type, false);
        }

        RuntimeValue result = NullValue.create();
        for (Statement statement : function.body)
        {
            result = Interpreter.evaluate(statement, scope);

            if (result.type == ValueType.Return) break;
        }

        RuntimeValue ret = result.type == ValueType.Return
                ? ((ReturnFlow) result).value
                : NullValue.create();

        ret = ClassMemberValue.mapToValue(ret);
        ErrorOr<Void> equality = TypeChecker.check(environment, ret, function.returnType);

        if (equality.isError())
            throw new ExpectedTypeNotMatch(String.format(
                "Tipo de retorno não condiz com o tipo esperado. %s",
                equality.error.getMessage()));

        return ret;
    }
}

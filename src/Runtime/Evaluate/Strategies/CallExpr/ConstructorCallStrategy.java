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
import Entities.Exceptions.InvalidCallException;
import Entities.Exceptions.Parser.InvalidStatementContextException;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Environment;
import Runtime.TypeChecker;
import Runtime.Interpreter;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.ClassValue;
import Runtime.Values.FunctionValue;
import Runtime.AccessChecker;

import java.util.ArrayList;

public class ConstructorCallStrategy implements CallExprStrategy
{
    @Override
    public RuntimeValue evaluate(CallExpr expr, RuntimeValue caller, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ClassValue target = (ClassValue) caller;

        ArrayList<RuntimeValue> args = new ArrayList<>();
        for (Expr arg : expr.arguments) args.add(Interpreter.evaluate(arg, environment));

        if (!target.members.containsKey(target.className))
        {
            if (!args.isEmpty())
            {
                throw new InvalidCallException("Não foi encontrado nenhum construtor " +
                        "com esse número de argumentos para esta classe.");
            }
            return target;
        }

        ClassMemberValue constructorMember = target.members.get(target.className);
        ErrorOr<Void> accessResult = AccessChecker.canAccess(constructorMember, environment.currentClass, target.className);

        if (accessResult.isError()) throw new InvalidCallException(accessResult.error.getMessage());

        if (constructorMember.value.type != ValueType.Function)
        {
            throw new InvalidCallException("Valor informado não permite ser chamado como um construtor.");
        }

        FunctionValue function = (FunctionValue) constructorMember.value;
        if (function.parameters.size() != expr.arguments.size())
        {
            throw new IncorrectNumberOfArgumentsException(String.format(
                    "O construtor %s esperava %d argumento(s), mas recebeu %d.",
                    function.name, function.parameters.size(), expr.arguments.size()));
        }

        Environment scope = Environment.create(function.declarationEnv, target);
        Type type = environment.resolveType(target.className).lookupType(target.className);

        if (target.parent != null)
        {
            scope.declareVariable(ReservedKeys.Super, target.parent, type, false);
        }
        scope.declareVariable(ReservedKeys.This, target, type, false);

        for (int i = 0; i < function.parameters.size(); i++)
        {
            ArgumentMetadata param = function.parameters.get(i);
            ErrorOr<Void> equality = TypeChecker.check(environment, ClassMemberValue.mapToValue(args.get(i)), param.getType());
            if (equality.isError())
            {
                throw new RuntimeException(String.format(
                        "Tipo incorreto informado para o argumento '%s'. %s",
                        param.getName(), equality.error.getMessage()));
            }
            scope.declareVariable(param.getName(), args.get(i), param.getType(), false, ClassMemberValue::mapToValue);
        }

        RuntimeValue result;
        for (Statement statement : function.body)
        {
            result = Interpreter.evaluate(statement, scope);
            if (result.type == ValueType.Return)
            {
                throw new InvalidStatementContextException(
                        "Não se pode haver um retorno em um construtor.");
            }
        }

        return target;
    }
}

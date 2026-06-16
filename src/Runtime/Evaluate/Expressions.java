package Runtime.Evaluate;

import Ast.Expressions.*;
import Ast.Expressions.Literals.ArrayLiteral;
import Ast.Expressions.Literals.ClassLiteral;
import Entities.Abstractions.Evaluate.Strategies.*;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.*;
import Entities.Exceptions.InvalidCallException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Exceptions.Parser.InvalidStatementContextException;
import Runtime.Environment;
import Runtime.Evaluate.Factory.AssignmentExpr.AssignmentExprFactory;
import Runtime.Evaluate.Factory.BinaryExpr.BinaryExprFactory;
import Runtime.Evaluate.Factory.CallExpr.CallExprFactory;
import Runtime.Evaluate.Factory.MemberExpr.MemberExprFactory;
import Runtime.Evaluate.Factory.UnaryExpr.UnaryExprFactory;
import Runtime.Interpreter;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Values.ClassValue;

import java.util.ArrayList;

public abstract class Expressions
{
    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment env)
    {
        return env.lookupVariable(identifier.value);
    }

    public static RuntimeValue evaluateUnaryExpr(UnaryExpr expr, Environment env)
        throws AlreadyDeclaredVariableException
    {
        ErrorOr<UnaryExprStrategy> result = UnaryExprFactory.build(expr);

        if (result.isError())
            throw new InvalidUnaryExpression(result.error.getMessage());

        RuntimeValue right = Interpreter.evaluate(expr.right, env);
        UnaryExprStrategy strategy = result.value;

        return strategy.evaluate(right, expr.operator);
    }

    public static RuntimeValue evaluateBinaryExpr(BinaryExpr expr, Environment env)
        throws AlreadyDeclaredVariableException
    {
        ErrorOr<BinaryExprStrategy> result = BinaryExprFactory.build(expr, env);

        if (result.isError())
            throw new InvalidBinaryOperation(result.error.getMessage());

        BinaryExprStrategy strategy = result.value;
        RuntimeValue left = Interpreter.evaluate(expr.left, env);
        RuntimeValue right = Interpreter.evaluate(expr.right, env);

        return strategy.evaluate(left, right, expr.operator);
    }

    public static RuntimeValue evaluateVariableAssignment(AssignmentExpr assignment, Environment env)
        throws AlreadyDeclaredVariableException
    {
        ErrorOr<AssignmentExprStrategy> result = AssignmentExprFactory.build(assignment);

        if (result.isError())
            throw new InvalidAssignmentExpression(result.error.getMessage());

        AssignmentExprStrategy strategy = result.value;

        return strategy.evaluate(assignment, env);
    }

    public static RuntimeValue evaluateMemberExpression(MemberExpr memberExpr, Environment env)
            throws AlreadyDeclaredVariableException
    {
        RuntimeValue owner = Interpreter.evaluate(memberExpr.object, env);

        ErrorOr<MemberExprStrategy> result = MemberExprFactory.build(memberExpr, owner);

        if (result.isError())
            throw new InvalidNodeException(result.error.getMessage());

        MemberExprStrategy strategy = result.value;

        return strategy.evaluate(memberExpr, owner, env);
    }

    public static RuntimeValue evaluateCallExpression(
        CallExpr call, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue caller = Interpreter.evaluate(call.caller, env);
        ErrorOr<CallExprStrategy> result = CallExprFactory.build(caller);

        if (result.isError())
            throw new InvalidCallException(result.error.getMessage());

        CallExprStrategy strategy = result.value;

        return strategy.evaluate(call, caller, env);
    }

    public static RuntimeValue evaluateObjectExpression(ObjectLiteral object, Environment env)
        throws AlreadyDeclaredVariableException
    {
        ObjectValue value = ObjectValue.create();

        for (Property prop : object.properties)
        {
            if (prop.value != null)
            {
                value.properties.put(prop.key, Interpreter.evaluate(prop.value, env));
                continue;
            }

            value.properties.put(prop.key, env.lookupVariable(prop.key));
        }

        return value;
    }

    public static RuntimeValue evaluateArrayExpression(ArrayLiteral array, Environment env)
        throws AlreadyDeclaredVariableException
    {
        ArrayValue value = ArrayValue.create();

        for (int i = 0; i < array.items.size(); i++) {
            Expr item = array.items.get(i);
            RuntimeValue evaluated = Interpreter.evaluate(item, env);
            value.items.put(i, evaluated);
        }

        return value;
    }

    public static RuntimeValue evaluateInstantiationExpression(ClassLiteral classLiteral, Environment env)
        throws AlreadyDeclaredVariableException
    {
        Environment declarationEnv = env.resolve(classLiteral.className);
        RuntimeValue declarationValue = declarationEnv.lookupVariable(classLiteral.className);

        if (declarationValue.type != ValueType.Class)
            throw new InvalidNodeException("Esperávamos um o nome de uma classe para instanciar.");

        ClassValue value = ((ClassValue) declarationValue).copy();

        if (!value.members.containsKey(classLiteral.className))
        {
            if (classLiteral.arguments.isEmpty()) return value;

            throw new InvalidCallException("Não foi encontrado nenhum construtor " +
                    "com esse número de argumentos para esta classe.");
        }

        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr expr : classLiteral.arguments) args.add(Interpreter.evaluate(expr, env));

        ClassMemberValue constructor = value.members.get(classLiteral.className);

        if (constructor.value.type != ValueType.Function)
            throw new InvalidCallException("Valor informado não permite ser chamado como um construtor.");

        FunctionValue function = (FunctionValue) constructor.value;
        Environment scope = Environment.create(function.declarationEnv);

        if (function.parameters.size() != classLiteral.arguments.size())
            throw new IncorrectNumberOfArgumentsException(String.format(
                "O construtor %s esperava %d argumento(s), mas recebeu %d.",
                function.name,
                function.parameters.size(),
                classLiteral.arguments.size()));

        Environment typeEnv = env.resolveType(value.className);
        Type type = typeEnv.lookupType(value.className);

        scope.declareVariable(ReservedKeys.This, value, type, false);

        for (int i = 0; i < function.parameters.size(); i++) {
            ArgumentMetadata param = function.parameters.get(i);
            String name = param.getName();

            ErrorOr<Void> equality = TypeChecker.check(env, args.get(i), param.getType());
            if (equality.isError())
                throw new RuntimeException(String.format(
                    "Tipo incorreto informado para o argumento '%s'. %s",
                    name,
                    equality.error.getMessage()));

            scope.declareVariable(name, args.get(i), param.getType(), false);
        }

        RuntimeValue result;
        for (Statement statement : function.body)
        {
            result = Interpreter.evaluate(statement, scope);

            if (result.type == ValueType.Return)
                throw new InvalidStatementContextException("Não se pode haver um retorno em um construtor.");
        }

        return value;
    }
}

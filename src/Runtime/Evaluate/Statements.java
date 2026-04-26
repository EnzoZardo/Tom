package Runtime.Evaluate;

import Ast.Expressions.Identifier;
import Ast.Expressions.Property;
import Ast.Statements.*;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Environment;
import Runtime.Interpreter;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statements
{
    public static RuntimeValue evaluateProgram(Program program, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue lastEvaluated = NullValue.create();

        for (Statement stmt : program.body)
        {
            lastEvaluated = Interpreter.evaluate(stmt, env);
        }

        return lastEvaluated;
    }

    public static RuntimeValue evaluateVariableDeclaration(
        VariableDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = declaration.value == null
                ? NullValue.create()
                : Interpreter.evaluate(declaration.value, env);

        if (!TypeChecker.check(env, value, declaration.expectedType) && declaration.value != null)
        {
            throw new ExpectedTypeNotMatch(String.format("Tipo incorreto informado para a variável '%s'.",
                declaration.identifier));
        }

        return env.declareVariable(declaration.identifier, value, declaration.expectedType, declaration.constant);
    }

    public static RuntimeValue evaluateTypeDeclaration(
        TypeDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException
    {
        env.declareType(declaration.identifier, Type.reduce(env, declaration.value));
        return NullValue.create();
    }

    public static RuntimeValue evaluateFunctionDeclaration(
        FunctionDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException
    {
        FunctionValue value = FunctionValue.createFromStatement(declaration, env);
        return env.declareConstant(value.name, value);
    }

    public static RuntimeValue evaluateScopeDeclaration(
        ScopeDeclaration scopeDeclaration,
        Environment env) throws AlreadyDeclaredVariableException
    {
        Environment scope = Environment.create(env);
        for (Statement statement : scopeDeclaration.body)
        {
            Interpreter.evaluate(statement, scope);
        }
        return NullValue.create();
    }

    public static RuntimeValue evaluateIfStatement(
        IfConditional ifStatement,
        Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = Interpreter.evaluate(ifStatement.test, env);

        if (value.bool())
        {
            return Interpreter.evaluate(ifStatement.consequent, env);
        }

        if (ifStatement.alternate != null)
        {
            return Interpreter.evaluate(ifStatement.alternate, env);
        }

        return NullValue.create();
    }

    public static RuntimeValue evaluateWhileStatement(
        While whileStatement,
        Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = Interpreter.evaluate(whileStatement.test, env);
        RuntimeValue ret = NullValue.create();
        while (value.bool())
        {
            ret = Interpreter.evaluate(whileStatement.consequent, env);
            value = Interpreter.evaluate(whileStatement.test, env);
        }

        return ret;
    }

    public static RuntimeValue evaluateForEachStatement(
            ForEach forEach,
            Environment env) throws AlreadyDeclaredVariableException
    {
        final int OBJECT_ARGS_SIZE = 3;
        final int ARGS_SIZE = 2;
        final int MINIMUM_ARGS_SIZE = 1;
        final String message = "Número incorreto de argumentos para o loop";
        RuntimeValue lastEvaluated = null;

        if (!ReservedKeys.In.equals(forEach.operator))
        {
            throw new InvalidBinaryOperation("Esperávamos o token 'em' para nosso loop para-cada.");
        }

        if (forEach.iterators.stream().anyMatch(x -> x.type != NodeType.Identifier))
        {
            throw new InvalidNodeException("Somente identificadores são aceitos em loops para-cada.");
        }

        RuntimeValue iterable = Interpreter.evaluate(forEach.iterable, env);

        if (forEach.iterators.size() > ARGS_SIZE && iterable.type != ValueType.Object
            || forEach.iterators.size() > OBJECT_ARGS_SIZE)
        {
            throw new IncorrectNumberOfArgumentsException(message);
        }

        List<Identifier> identifiers = forEach.iterators
            .stream()
            .map(x -> (Identifier) x)
            .toList();

        for (int i = 0; i < iterable.iteratorSize() - 1; i++)
        {
            Environment operationEnv = Environment.create(env);

            switch (identifiers.size())
            {
                case MINIMUM_ARGS_SIZE -> operationEnv.declareConstant(identifiers.getFirst().value, iterable.iterate(i));
                case ARGS_SIZE ->
                {
                    operationEnv.declareConstant(identifiers.getFirst().value, NumericValue.create(i, true));
                    operationEnv.declareConstant(identifiers.get(1).value, iterable.iterate(i));
                }
                case OBJECT_ARGS_SIZE ->
                {
                    ArrayValue iterated = (ArrayValue) iterable.iterate(i);
                    HashMap<Integer, RuntimeValue> value = iterated.items;
                    operationEnv.declareConstant(identifiers.getFirst().value, NumericValue.create(i, true));
                    operationEnv.declareConstant(identifiers.get(1).value, value.get(0));
                    operationEnv.declareConstant(identifiers.getLast().value, value.get(1));
                }
                default -> throw new IncorrectNumberOfArgumentsException(message);
            }

            lastEvaluated = Interpreter.evaluate(forEach.consequent, operationEnv);
        }

        return lastEvaluated;
    }
}

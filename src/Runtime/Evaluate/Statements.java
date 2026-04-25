package Runtime.Evaluate;

import Ast.Statements.*;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Runtime.Environment;
import Runtime.Interpreter;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.FunctionValue;
import Runtime.Values.NullValue;
import Runtime.TypeChecker;

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
            throw new ExpectedTypeNotMatch(String.format("Tipo incorreto informado para a variável '%s'.", declaration.identifier));
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
        IfStatement ifStatement,
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
        WhileStatement whileStatement,
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
}

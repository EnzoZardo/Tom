package Runtime.Evaluate;

import Ast.Statements.*;
import Ast.Statements.Types.Type;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.ExpectedTypeNotMatch;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Types.RuntimeValue;
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
            throw new ExpectedTypeNotMatch(String.format("Wrong type informed for variable %s", declaration.identifier));
        }

        return env.declareVariable(declaration.identifier, value, declaration.constant);
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
}

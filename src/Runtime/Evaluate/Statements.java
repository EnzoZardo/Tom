package Runtime.Evaluate;

import Ast.Statements.Program;
import Ast.Statements.VariableDeclaration;
import Ast.Types.Statement;
import Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Types.RuntimeValue;
import Runtime.Values.NullValue;

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

        return env.declareVariable(declaration.identifier, value, declaration.constant);
    }
}

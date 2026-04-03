package Runtime.Values;

import Ast.Statements.FunctionDeclaration;
import Ast.Types.Statement;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;
import Types.Pair;

import java.util.ArrayList;
import java.util.function.Function;
import Runtime.Environment;

public class FunctionValue extends RuntimeValue
{
    public String name;
    public ArrayList<Statement> body;
    public ArrayList<String> parameters;
    public Environment declarationEnv;

    protected FunctionValue(
        String name,
        ArrayList<Statement> body,
        ArrayList<String> parameters,
        Environment declarationEnv)
    {
        super(ValueType.Function);
        this.name = name;
        this.body = body;
        this.parameters = parameters;
        this.declarationEnv = declarationEnv;
    }

    public static FunctionValue create(
            String name,
            ArrayList<Statement> body,
            ArrayList<String> parameters,
            Environment declarationEnv)
    {
        return new FunctionValue(name, body, parameters, declarationEnv);
    }

    public static FunctionValue createFromStatement(FunctionDeclaration statement, Environment env)
    {
        return new FunctionValue(statement.identifier, statement.body, statement.parameters, env);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }
}

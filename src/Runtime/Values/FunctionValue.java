package Runtime.Values;

import Ast.Statements.FunctionDeclaration;
import Ast.Statements.Statement;
import Ast.Statements.Types.Type;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

import java.util.ArrayList;

import Runtime.Environment;
import Types.ArgumentMetadata;

public class FunctionValue extends RuntimeValue
{
    public String name;
    public ArrayList<Statement> body;
    public ArrayList<ArgumentMetadata> parameters;
    public Type returnType;
    public Environment declarationEnv;

    protected FunctionValue(
        String name,
        ArrayList<Statement> body,
        ArrayList<ArgumentMetadata> parameters,
        Type returnType,
        Environment declarationEnv)
    {
        super(ValueType.Function);
        this.name = name;
        this.body = body;
        this.parameters = parameters;
        this.returnType = returnType;
        this.declarationEnv = declarationEnv;
    }

    public static FunctionValue create(
            String name,
            ArrayList<Statement> body,
            ArrayList<ArgumentMetadata> parameters,
            Type returnType,
            Environment declarationEnv)
    {
        return new FunctionValue(name, body, parameters, returnType, declarationEnv);
    }

    public static FunctionValue createFromStatement(FunctionDeclaration statement, Environment env)
    {
        return new FunctionValue(statement.identifier, statement.body, statement.parameters, statement.returnType, env);
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

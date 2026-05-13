package Runtime.Values;

import Ast.Statements.FunctionDeclaration;
import Ast.Types.FunctionType;
import Entities.Abstractions.Ast.Statement;
import Entities.Abstractions.Type;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

import java.util.ArrayList;
import java.util.stream.Collectors;

import Runtime.Environment;
import Entities.Metadata.ArgumentMetadata;

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

    public Type type() {
        return FunctionType.create(
            parameters.stream().map(ArgumentMetadata::getType).collect(Collectors.toCollection(ArrayList::new)),
            returnType);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        if (that.type != type) {
            return false;
        }

        FunctionValue functionValue = (FunctionValue) that;

        if (parameters.size() != functionValue.parameters.size())
        {
            return false;
        }

        if (Type.equals(returnType, functionValue.returnType).isError()) {
            return false;
        }

        for (int i = 0; i < parameters.size(); i++)
        {
            if (Type.equals(parameters.get(i).getType(), functionValue.parameters.get(i).getType()).isError()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean bool()
    {
        return true;
    }


    @Override
    public String toString()
    {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++)
        {
            params.append(parameters.get(i));
            if (i < parameters.size() - 1) {
                params.append(", ");
            }
        }
        return ReservedKeys.Function + " " + name + "(" +  params + "): " + returnType;
    }
}

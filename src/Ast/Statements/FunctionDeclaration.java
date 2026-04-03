package Ast.Statements;

import Ast.Types.Enums.NodeType;

import java.util.ArrayList;

public class FunctionDeclaration extends Expr
{
    public String identifier;
    public ArrayList<Expr> arguments;

    protected FunctionDeclaration(
        String identifier,
        ArrayList<Expr> arguments)
    {
        super(NodeType.FunctionDeclaration);
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public static FunctionDeclaration create(
        String identifier,
        ArrayList<Expr> arguments)
    {
        return new FunctionDeclaration(identifier, arguments);
    }

    public static FunctionDeclaration create(
            String identifier)
    {
        return new FunctionDeclaration(identifier, new ArrayList<>());
    }

    private String printArgs(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[\n");
        for (Expr entry : arguments)
        {
            ret.repeat("\t", next)
                    .append(entry.print(next))
                    .append(',');
        }
        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "arguments: " + printArgs(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

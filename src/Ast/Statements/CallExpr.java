package Ast.Statements;

import Ast.Types.Enums.NodeType;
import java.util.ArrayList;

public class CallExpr extends Expr
{
    public final Expr caller;
    public ArrayList<Expr> arguments;

    protected CallExpr(
            Expr caller,
            ArrayList<Expr> arguments)
    {
        super(NodeType.CallExpression);
        this.arguments = arguments;
        this.caller = caller;
    }

    public static CallExpr create(
            Expr caller,
            ArrayList<Expr> arguments)
    {
        return new CallExpr(caller, arguments);
    }

    private String printArgs(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[");
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
                "\t".repeat(next) + "caller: " + caller.print(next) + ",\n" +
                "\t".repeat(next) + "args: " + printArgs(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
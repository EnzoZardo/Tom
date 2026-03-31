package Ast.Statements;

import Ast.Types.Enums.NodeType;
import java.util.ArrayList;

public class CallExpr extends Expr
{
    public final Expr caller;
    public ArrayList<Expr> args;

    protected CallExpr(
            Expr caller,
            ArrayList<Expr> args)
    {
        super(NodeType.CallExpression);
        this.args = args;
        this.caller = caller;
    }

    public static CallExpr create(
            Expr caller,
            ArrayList<Expr> args)
    {
        return new CallExpr(caller, args);
    }

    private String printArgs(int level)
    {
        StringBuilder ret = new StringBuilder("\n")
                .append("\t".repeat(level))
                .append("[\n");
        for (Expr entry : args)
        {
            ret.append("\t".repeat(level + 1))
                    .append(entry.print(level + 1))
                    .append(',');
        }
        return ret.append("\n")
                .append("\t".repeat(level))
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "caller: " + caller.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "args: " + printArgs(level + 1) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
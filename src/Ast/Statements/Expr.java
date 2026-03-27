package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;

public class Expr extends Statement
{
    public Expr(NodeType type)
    {
        super(type);
    }

    @Override
    public String print(int level) {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }
}

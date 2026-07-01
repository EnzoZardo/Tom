package Entities.Abstractions.Ast;

import Entities.Enums.Ast.NodeType;
import Ast.Expressions.AssignmentExpr;
import Parser.Parser;

public abstract class Expr extends Statement
{
    public Expr(NodeType type)
    {
        super(type);
    }

    // Começa na expressão de menor precedência
    public static Expr parse(Parser parser)
    {
        return AssignmentExpr.parse(parser);
    }

    @Override
    public String print(int level) {
        return "\n" +
            "\t".repeat(level) + "{\n" +
            "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
            "\t".repeat(level) + "}";
    }
}

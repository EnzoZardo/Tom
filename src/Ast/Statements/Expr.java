package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Parser.Parser;

public class Expr extends Statement
{
    public Expr(NodeType type)
    {
        super(type);
    }

    // Começa na expressão de menor precedência
    public static Expr parse(Parser parser) throws InvalidArgumentException, InvalidTokenException
    {
        return AssignmentExpr.parse(parser);
    }

    @Override
    public String print(int level) {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }
}

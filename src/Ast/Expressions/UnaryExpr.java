package Ast.Expressions;

import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Parser.Parser;

public class UnaryExpr extends Expr
{
    public Expr right;
    public String operator;

    protected UnaryExpr(Expr right, String operator)
    {
        super(NodeType.UnaryExpr);
        this.right = right;
        this.operator = operator;
    }

    public static UnaryExpr create(Expr right, String operator)
    {
        return new UnaryExpr(right, operator);
    }

    public static Expr parse(Parser parser) throws InvalidArgumentException
    {
        if (ReservedOperators.isUnary(parser.peekValue()))
        {
            String operator = parser.consume().value;
            Expr right = UnaryExpr.parse(parser);
            return UnaryExpr.create(right, operator);
        }

        return BinaryExpr.parseBoolean(parser);
    }

    @Override
    public String print(int level) {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "right: " + right.print(next) + ",\n" +
                "\t".repeat(next) + "operator: " + operator + ",\n" +
                "\t".repeat(level) + "}";
    }
}


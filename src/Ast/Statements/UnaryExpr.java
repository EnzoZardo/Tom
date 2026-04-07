package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Constants.ReservedKeys;
import Constants.ReservedOperators;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Parser.Parser;

public class UnaryExpr extends Expr
{
    public Expr right;
    public String operator;

    protected UnaryExpr(
            Expr right,
            String operator)
    {
        super(NodeType.UnaryExpr);
        this.right = right;
        this.operator = operator;
    }

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        if (ReservedOperators.isUnary(parser.peekValue()))
        {
            String operator = parser.consume().value;
            Expr right = UnaryExpr.parse(parser);
            return UnaryExpr.create(right, operator);
        }

        return BinaryExpr.parseBoolean(parser);
    }

    public static UnaryExpr create(
            Expr right,
            String operator)
    {
        return new UnaryExpr(right, operator);
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


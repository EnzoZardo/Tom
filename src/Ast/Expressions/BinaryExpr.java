package Ast.Expressions;

import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Parser.Parser;

public class BinaryExpr extends Expr
{
    public Expr left;
    public Expr right;
    public String operator;

    protected BinaryExpr(
        Expr left,
        Expr right,
        String operator)
    {
        super(NodeType.BinaryExpr);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static ErrorOr<Expr> parseBoolean(Parser parser)
    {
        ErrorOr<Expr> leftOr = BinaryExpr.parseAdditive(parser);
        if (leftOr.isError()) return leftOr.propagateError();
        Expr left = leftOr.value;

        while (ReservedOperators.isBooleanOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;

            ErrorOr<Expr> rightOr = BinaryExpr.parseAdditive(parser);
            if (rightOr.isError()) return rightOr.propagateError();

            left = BinaryExpr.create(left, rightOr.value, operator);
        }

        return ErrorOr.Success(left);
    }

    public static ErrorOr<Expr> parseMultiplicative(Parser parser)
    {
        ErrorOr<Expr> leftOr = UnaryExpr.parse(parser);
        if (leftOr.isError()) return leftOr.propagateError();

        Expr left = leftOr.value;

        while (BinaryExpr.isMultiplicativeOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;

            ErrorOr<Expr> rightOr = UnaryExpr.parse(parser);
            if (rightOr.isError()) return rightOr.propagateError();

            left = BinaryExpr.create(left, rightOr.value, operator);
        }

        return ErrorOr.Success(left);
    }

    public static ErrorOr<Expr> parseAdditive(Parser parser)
    {
        ErrorOr<Expr> leftOr = BinaryExpr.parseMultiplicative(parser);
        if (leftOr.isError()) return leftOr.propagateError();

        Expr left = leftOr.value;

        while (ReservedOperators.isAdditiveOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;

            ErrorOr<Expr> rightOr = BinaryExpr.parseMultiplicative(parser);
            if (rightOr.isError()) return rightOr.propagateError();

            left = BinaryExpr.create(left, rightOr.value, operator);
        }

        return ErrorOr.Success(left);
    }

    private static boolean isMultiplicativeOperator(String operator)
    {
        return ReservedKeys.IntegerDivision.equals(operator)
            || ReservedKeys.Multiplication.equals(operator)
            || ReservedKeys.Division.equals(operator)
            || ReservedKeys.Mod.equals(operator);
    }

    public static BinaryExpr create(
        Expr left,
        Expr right,
        String operator)
    {
        return new BinaryExpr(left, right, operator);
    }

    @Override
    public String print(int level) {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "node: " + type.toString() + ",\n" +
            "\t".repeat(next) + "left: " + left.print(next) + ",\n" +
            "\t".repeat(next) + "right: " + right.print(next) + ",\n" +
            "\t".repeat(next) + "operator: " + operator + ",\n" +
            "\t".repeat(level) + "}";
    }
}

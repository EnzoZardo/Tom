package Ast.Expressions;

import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
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

    public static Expr parseBoolean(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr left = MemberExpr.parseCall(parser);

        while (ReservedOperators.isBooleanOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;
            Expr right = MemberExpr.parseCall(parser);
            left = BinaryExpr.create(left, right, operator);
        }

        return left;
    }

    public static Expr parseMultiplicative(Parser parser) throws InvalidArgumentException, InvalidTokenException
    {
        Expr left = UnaryExpr.parse(parser);

        while (BinaryExpr.isMultiplicativeOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;
            Expr right = UnaryExpr.parse(parser);
            left = BinaryExpr.create(left, right, operator);
        }

        return left;
    }

    public static Expr parseAdditive(Parser parser) throws InvalidArgumentException, InvalidTokenException
    {
        Expr left = BinaryExpr.parseMultiplicative(parser);

        while (ReservedOperators.isAdditiveOperator(parser.peekValue()))
        {
            String operator = parser.consume().value;
            Expr right = BinaryExpr.parseMultiplicative(parser);
            left = BinaryExpr.create(left, right, operator);
        }

        return left;
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

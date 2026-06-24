package Ast.Expressions;

import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
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

    public static ErrorOr<Expr> parse(Parser parser)
    {
        if (ReservedOperators.isUnary(parser.peekValue()))
        {
            String operator = parser.consume().value;

            ErrorOr<Expr> rightOr = UnaryExpr.parse(parser);
            if (rightOr.isError()) return rightOr.propagateError();

            return ErrorOr.Success(UnaryExpr.create(rightOr.value, operator));
        }

        return MemberExpr.parseCall(parser);
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


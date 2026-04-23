package Ast.Expressions;

import Ast.Expressions.Literals.ArrayLiteral;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
import Parser.Parser;

public class AssignmentExpr extends Expr
{
    public Expr assigned;
    public Expr value;

    protected AssignmentExpr(
            Expr assigned,
            Expr right)
    {
        super(NodeType.AssignmentExpression);
        this.assigned = assigned;
        this.value = right;
    }

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr left = BinaryExpr.parseAdditive(parser);

        if (parser.peekIs(TokenType.EQUALS))
        {
            parser.consume();
            Expr value = AssignmentExpr.parse(parser);
            return AssignmentExpr.create(left, value);
        }

        return left;
    }

    public static AssignmentExpr create(Expr assigned, Expr value)
    {
        return new AssignmentExpr(assigned, value);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "assigned: " + assigned.print(next) + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

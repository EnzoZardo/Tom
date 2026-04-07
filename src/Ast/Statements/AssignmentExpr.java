package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
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
        Expr left = ObjectLiteral.parse(parser);

        if (parser.peekIs(TokenType.EQUALS))
        {
            parser.consume();
            Expr value = AssignmentExpr.parse(parser);
            return AssignmentExpr.create(left, value);
        }

        return left;
    }

    public static AssignmentExpr create(
            Expr assigned,
            Expr value)
    {
        return new AssignmentExpr(assigned, value);
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "assigned: " + assigned.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "value: " + value.print(level + 1) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

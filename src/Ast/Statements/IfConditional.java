package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Parser.Parser;

public class IfConditional extends Statement
{
    public Expr test;
    public Statement consequent;
    public Statement alternate;

    protected IfConditional(
        Expr test,
        Statement consequent,
        Statement alternate)
    {
        super(NodeType.IfStatement);
        this.test = test;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public static IfConditional create(
        Expr test,
        Statement consequent,
        Statement alternate)
    {
        return new IfConditional(test, consequent, alternate);
    }

    public static IfConditional create(
        Expr test,
        Statement consequent)
    {
        return new IfConditional(test, consequent, null);
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        parser.consume();
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' após um se.");
        Expr test = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' após a expressão de teste de um se.");
        Statement consequent = Statement.parse(parser);

        if (parser.peekIs(TokenType.ELSE))
        {
            parser.consume();
            Statement alternate = Statement.parse(parser);
            return IfConditional.create(test, consequent, alternate);
        }

        return IfConditional.create(test, consequent);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "test: " + test.print(next) + ",\n" +
                "\t".repeat(next) + "consequent: " + consequent.print(next) + ",\n" +
                "\t".repeat(next) + "alternate: " + (alternate == null ? null : alternate.print(next)) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

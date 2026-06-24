package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
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

    public static ErrorOr<IfConditional> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Token> openOr = parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' após um se.");
        if (openOr.isError()) return openOr.propagateError();
        ErrorOr<Expr> testOr = Expr.parse(parser);
        if (testOr.isError()) return testOr.propagateError();
        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' após a expressão de teste de um se.");
        if (closeOr.isError()) return closeOr.propagateError();
        var consequentOr = Statement.parse(parser);
        if (consequentOr.isError()) return consequentOr.propagateError();

        if (parser.peekIs(TokenType.ELSE))
        {
            parser.consume();
            var alternateOr = Statement.parse(parser);
            if (alternateOr.isError()) return alternateOr.propagateError();
            return ErrorOr.Success(IfConditional.create(testOr.value, consequentOr.value, alternateOr.value));
        }

        return ErrorOr.Success(IfConditional.create(testOr.value, consequentOr.value));
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
                "\t".repeat(next) + "alternate: " + (alternate == null ? null : alternate.print(next)) + "\n" +
                "\t".repeat(level) + "}";
    }
}

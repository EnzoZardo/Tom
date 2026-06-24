package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class While extends Statement
{
    public Expr test;
    public Statement consequent;

    protected While(
            Expr test,
            Statement consequent)
    {
        super(NodeType.WhileStatement);
        this.test = test;
        this.consequent = consequent;
    }

    public static While create(Expr test, Statement consequent)
    {
        return new While(test, consequent);
    }

    public static ErrorOr<While> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Token> openOr = parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' após um enquanto.");
        if (openOr.isError()) return openOr.propagateError();

        ErrorOr<Expr> testOr = Expr.parse(parser);
        if (testOr.isError()) return testOr.propagateError();

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' após a expressão de teste de um enquanto.");
        if (closeOr.isError()) return closeOr.propagateError();

        parser.context.enterLoop();
        var consequentOr = Statement.parse(parser);
        if (consequentOr.isError()) return consequentOr.propagateError();
        parser.context.outLoop();

        return ErrorOr.Success(While.create(testOr.value, consequentOr.value));
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "test: " + test.print(next) + ",\n" +
                "\t".repeat(next) + "consequent: " + consequent.print(next) + "\n" +
                "\t".repeat(level) + "}";
    }
}

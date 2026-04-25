package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Parser.Parser;

public class WhileStatement extends Statement
{
    public Expr test;
    public Statement consequent;

    protected WhileStatement(
            Expr test,
            Statement consequent)
    {
        super(NodeType.WhileStatement);
        this.test = test;
        this.consequent = consequent;
    }

    public static WhileStatement create(Expr test, Statement consequent)
    {
        return new WhileStatement(test, consequent);
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        parser.consume();
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' após um enquanto.");
        Expr test = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' após a expressão de teste de um enquanto.");
        Statement consequent = Statement.parse(parser);

        return WhileStatement.create(test, consequent);
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
                "\t".repeat(level) + "}";
    }
}

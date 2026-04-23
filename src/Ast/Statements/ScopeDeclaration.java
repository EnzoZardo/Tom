package Ast.Statements;

import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Parser.Parser;

import java.util.ArrayList;

public class ScopeDeclaration extends Statement
{
    public ArrayList<Statement> body;

    protected ScopeDeclaration(
        ArrayList<Statement> body)
    {
        super(NodeType.ScopeDeclaration);
        this.body = body;
    }

    public static ScopeDeclaration create(ArrayList<Statement> body) {
        return new ScopeDeclaration(body);
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        if (parser.peekIs(1, TokenType.IDENTIFIER) && parser.peekIs(2, TokenType.COLON))
        {
            return Expr.parse(parser);
        }

        parser.consume();
        ArrayList<Statement> body = new ArrayList<>();

        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            body.add(Statement.parse(parser));
        }

        parser.consume();
        return ScopeDeclaration.create(body);
    }

    private String printBody(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[");

        for (Statement statement : body)
        {
            ret.repeat("\t", next)
                    .append(statement.print(next))
                    .append(',');
        }
        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "body: " + printBody(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

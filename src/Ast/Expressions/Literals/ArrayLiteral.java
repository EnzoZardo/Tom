package Ast.Expressions.Literals;

import Entities.Abstractions.Ast.Expr;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Parser.Parser;

import java.util.ArrayList;

public class ArrayLiteral extends Expr
{
    public final ArrayList<Expr> items;

    protected ArrayLiteral(ArrayList<Expr> items)
    {
        super(NodeType.ArrayLiteral);
        this.items = items;
    }

    public static ArrayLiteral create(ArrayList<Expr> items) throws InvalidArgumentException
    {
        return new ArrayLiteral(items);
    }

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        if (!parser.peekIs(TokenType.OPEN_BRACKETS))
        {
            return ObjectLiteral.parse(parser);
        }

        parser.consume();

        ArrayList<Expr> items = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACKETS))
        {
            items.add(Expr.parse(parser));

            if (!parser.peekIs(TokenType.CLOSE_BRACKETS))
            {
                parser.expect(TokenType.COMMA, "Esperávamos ',' ou ']' " +
                        "para a fechar uma lista mas recebemos '%s'.");
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }
        }

        parser.expect(TokenType.CLOSE_BRACKETS, "Esperávamos ']' para fechar a lista, mas recebemos '%s'.");
        return ArrayLiteral.create(items);
    }

    private String printItems(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append('[');

        for (Expr item : items)
        {
            ret.repeat("\t", next)
                    .append(item.print(next))
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
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "items: " + printItems(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}


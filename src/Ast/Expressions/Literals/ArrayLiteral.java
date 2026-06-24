package Ast.Expressions.Literals;

import Entities.Abstractions.Ast.Expr;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
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

    public static ArrayLiteral create(ArrayList<Expr> items)
    {
        return new ArrayLiteral(items);
    }

    public static ErrorOr<Expr> parse(Parser parser)
    {
        parser.consume();

        ArrayList<Expr> items = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACKETS))
        {
            ErrorOr<Expr> itemOr = Expr.parse(parser);
            if (itemOr.isError()) return itemOr.propagateError();
            items.add(itemOr.value);

            if (!parser.peekIs(TokenType.CLOSE_BRACKETS))
            {
                ErrorOr<Token> closeOr = parser.expect(TokenType.COMMA, "Na criação de uma lista, esperávamos " +
                    "uma vírgula - , - ou um fechamento de colchetes - ] -, mas recebemos outro símbolo no código - %s");
                if (closeOr.isError()) return closeOr.propagateError();
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }
        }

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_BRACKETS, "Esperávamos ']' para fechar a lista, " +
            "mas recebemos outro símbolo no código - %s");
        if (closeOr.isError()) return closeOr.propagateError();
        return ErrorOr.Success(ArrayLiteral.create(items));
    }

    private String printItems(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
            .append('[');

        for (Expr item : items)
            ret.repeat("\t", next)
                .append(item.print(next))
                .append(',');

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


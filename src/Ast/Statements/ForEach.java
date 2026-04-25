package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;

public class ForEach extends Statement
{
    public Expr iterable;
    public ArrayList<Expr> iterators;
    public String operator;
    public Statement consequent;

    protected ForEach(
            Expr iterable,
            ArrayList<Expr> iterators,
            String operator,
            Statement consequent)
    {
        super(NodeType.ForEachStatement);
        this.consequent = consequent;
        this.iterators = iterators;
        this.operator = operator;
        this.iterable = iterable;
    }

    public static ForEach create(
            Expr iterable,
            ArrayList<Expr> iterators,
            String operator,
            Statement consequent)
    {
        return new ForEach(iterable, iterators, operator, consequent);
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        parser.consume();
        parser.expect(TokenType.EACH, "Esperávamos a palavra chave 'cada' após um 'para'.");
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' após um loop para cada.");
        ArrayList<Expr> iterators = parseArgumentsList(parser);
        Token operator = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos uma expressão binária entre os " +
                "argumentos e nosso iterável.");
        Expr iterable = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' após a expressão de teste de um enquanto.");
        Statement consequent = Statement.parse(parser);

        return ForEach.create(iterable, iterators, operator.value, consequent);
    }

    public static ArrayList<Expr> parseArgumentsList(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        ArrayList<Expr> args = new ArrayList<>();
        args.add(Expr.parse(parser));

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();
            args.add(Expr.parse(parser));
        }

        return args;
    }


    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "consequent: " + consequent.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

package Ast.Statements;

import Ast.Expressions.PrimaryExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
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

    public static ForEach parse(Parser parser)
    {
        parser.consume();
        parser.expect(TokenType.EACH, "Esperávamos a palavra chave 'cada' após um " +
            "'para' para podermos iniciar um loop para-cada, mas recebemos outro símbolo no código - %s");

        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir o nosso loop para-cada, mas recebemos outro símbolo no código - %s");

        ArrayList<Expr> iterators = parseArgumentsList(parser);

        Token operator = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos uma expressão binária " +
            "entre os argumentos e o iterável de nosso para-cada, mas recebemos outro símbolo no nosso código - %s");

        Expr iterable = Expr.parse(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar o nosso loop para-cada, mas recebemos outro símbolo no código - %s");

        parser.context.enterLoop();
        Statement consequent = Statement.parse(parser);
        parser.context.outLoop();

        return ForEach.create(iterable, iterators, operator.value, consequent);
    }

    public static ArrayList<Expr> parseArgumentsList(Parser parser)
    {
        ArrayList<Expr> args = new ArrayList<>();
        args.add(PrimaryExpr.parse(parser));

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();
            args.add(PrimaryExpr.parse(parser));
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
                "\t".repeat(next) + "consequent: " + consequent.print(next) + "\n" +
                "\t".repeat(level) + "}";
    }
}

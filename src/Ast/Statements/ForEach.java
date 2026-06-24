package Ast.Statements;

import Ast.Expressions.PrimaryExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
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

    public static ErrorOr<ForEach> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Token> eachOr = parser.expect(TokenType.EACH, "Esperávamos a palavra chave 'cada' após um " +
            "'para' para podermos iniciar um loop para-cada, mas recebemos outro símbolo no código - %s");
        if (eachOr.isError()) return eachOr.propagateError();

        ErrorOr<Token> openOr = parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir o nosso loop para-cada, mas recebemos outro símbolo no código - %s");
        if (openOr.isError()) return openOr.propagateError();

        ErrorOr<ArrayList<Expr>> iteratorsOr = parseArgumentsList(parser);
        if (iteratorsOr.isError()) return iteratorsOr.propagateError();

        ErrorOr<Token> operatorOr = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos uma expressão binária " +
            "entre os argumentos e o iterável de nosso para-cada, mas recebemos outro símbolo no nosso código - %s");
        if (operatorOr.isError()) return operatorOr.propagateError();

        ErrorOr<Expr> iterableOr = Expr.parse(parser);
        if (iterableOr.isError()) return iterableOr.propagateError();

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar o nosso loop para-cada, mas recebemos outro símbolo no código - %s");
        if (closeOr.isError()) return closeOr.propagateError();

        parser.context.enterLoop();
        var consequentOr = Statement.parse(parser);
        if (consequentOr.isError()) return consequentOr.propagateError();
        parser.context.outLoop();

        return ErrorOr.Success(ForEach.create(iterableOr.value, iteratorsOr.value, operatorOr.value.value, consequentOr.value));
    }

    public static ErrorOr<ArrayList<Expr>> parseArgumentsList(Parser parser)
    {
        ArrayList<Expr> args = new ArrayList<>();
        ErrorOr<Expr> firstOr = PrimaryExpr.parse(parser);
        if (firstOr.isError()) return firstOr.propagateError();
        args.add(firstOr.value);

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();
            ErrorOr<Expr> argOr = PrimaryExpr.parse(parser);
            if (argOr.isError()) return argOr.propagateError();
            args.add(argOr.value);
        }

        return ErrorOr.Success(args);
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

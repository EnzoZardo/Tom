package Ast.Expressions;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;
import Entities.Metadata.ExprMetadata;

import java.util.ArrayList;

public class CallExpr extends Expr
{
    public final Expr caller;
    public ArrayList<Expr> arguments;

    protected CallExpr(
        Expr caller,
        ArrayList<Expr> arguments)
    {
        super(NodeType.CallExpression);
        this.arguments = arguments;
        this.caller = caller;
    }

    public static CallExpr create(
        Expr caller,
        ArrayList<Expr> arguments)
    {
        return new CallExpr(caller, arguments);
    }

    public static ErrorOr<Expr> parse(Parser parser, Expr caller)
    {
        ErrorOr<ArrayList<Expr>> argsOr = CallExpr.parseArgs(parser);
        if (argsOr.isError()) return argsOr.propagateError();

        Expr call = CallExpr.create(caller, argsOr.value);

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            return CallExpr.parse(parser, call);

        return ErrorOr.Success(call);
    }

    public static ErrorOr<ArrayList<Expr>> parseArgumentsList(Parser parser)
    {
        ArrayList<Expr> args = new ArrayList<>();

        ErrorOr<Expr> firstOr = Expr.parse(parser);
        if (firstOr.isError()) return firstOr.propagateError();

        args.add(firstOr.value);

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();

            ErrorOr<Expr> argOr = Expr.parse(parser);
            if (argOr.isError()) return argOr.propagateError();

            args.add(argOr.value);
        }

        return ErrorOr.Success(args);
    }

    private static ErrorOr<Void> parseArgDeclaration(ArrayList<ExprMetadata> args, Parser parser)
    {
        ErrorOr<Expr> identifierOr = AssignmentExpr.parse(parser);
        if (identifierOr.isError()) return identifierOr.propagateError();

        ErrorOr<Token> colonOr = parser.expect(TokenType.COLON,
            "Esperávamos dois pontos - : - para o nome de um parâmetro de nossa função, mas recebemos " +
            "outro símbolo no código - %s");
        if (colonOr.isError()) return colonOr.propagateError();

        ErrorOr<Type> typeOr = Type.parse(parser);
        if (typeOr.isError()) return typeOr.propagateError();

        args.add(ExprMetadata.create(typeOr.value, identifierOr.value));
        return ErrorOr.Success();
    }

    public static ErrorOr<ArrayList<ExprMetadata>> parseArgumentsDeclarationList(Parser parser)
    {
        ArrayList<ExprMetadata> args = new ArrayList<>();

        ErrorOr<Void> firstOr = parseArgDeclaration(args, parser);
        if (firstOr.isError()) return firstOr.propagateError();

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();

            ErrorOr<Void> nextOr = parseArgDeclaration(args, parser);
            if (nextOr.isError()) return nextOr.propagateError();
        }

        return ErrorOr.Success(args);
    }

    public static ErrorOr<ArrayList<Expr>> parseArgs(Parser parser)
    {
        ErrorOr<Token> openOr = parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir a lista de argumentos de uma função, mas recebemos outro símbolo no código - %s");
        if (openOr.isError()) return openOr.propagateError();

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return ErrorOr.Success(new ArrayList<>());
        }

        ErrorOr<ArrayList<Expr>> argsOr = CallExpr.parseArgumentsList(parser);
        if (argsOr.isError()) return argsOr.propagateError();

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar a lista de argumentos de uma função, mas recebemos outro " +
            "símbolo no código - %s");
        if (closeOr.isError()) return closeOr.propagateError();

        return argsOr;
    }

    public static ErrorOr<ArrayList<ExprMetadata>> parseArgsDeclaration(Parser parser)
    {
        ErrorOr<Token> openOr = parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir a lista de parâmetros de uma função, mas recebemos outro símbolo no código - %s");
        if (openOr.isError()) return openOr.propagateError();

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return ErrorOr.Success(new ArrayList<>());
        }

        ErrorOr<ArrayList<ExprMetadata>> argsOr = CallExpr.parseArgumentsDeclarationList(parser);
        if (argsOr.isError()) return argsOr.propagateError();

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar a lista de parâmetros de uma função, mas recebemos outro " +
            "símbolo no código - %s");
        if (closeOr.isError()) return closeOr.propagateError();

        return argsOr;
    }

    private String printArgs(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[");

        for (Expr entry : arguments)
            ret.repeat("\t", next)
                    .append(entry.print(next))
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
                "\t".repeat(next) + "caller: " + caller.print(next) + ",\n" +
                "\t".repeat(next) + "args: " + printArgs(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
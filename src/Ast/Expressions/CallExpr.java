package Ast.Expressions;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Type;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
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

    public static Expr parse(Parser parser, Expr caller) throws InvalidTokenException, InvalidArgumentException
    {
        Expr call = CallExpr.create(caller, CallExpr.parseArgs(parser));

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
        {
            call = CallExpr.parse(parser, call);
        }

        return call;
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

    private static void parseArgDeclaration(ArrayList<ExprMetadata> args, Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr identifier = AssignmentExpr.parse(parser);
        parser.expect(TokenType.COLON, "Esperávamos ':' após o identificador de um argumento.");
        Type type = Type.parse(parser);
        args.add(ExprMetadata.create(type, identifier));
    }

    public static ArrayList<ExprMetadata> parseArgumentsDeclarationList(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        ArrayList<ExprMetadata> args = new ArrayList<>();
        parseArgDeclaration(args, parser);

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();
            parseArgDeclaration(args, parser);
        }

        return args;
    }

    public static ArrayList<Expr> parseArgs(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' para abrir a lista de argumentos.");

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return new ArrayList<>();
        }

        ArrayList<Expr> args = CallExpr.parseArgumentsList(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' para fechar a lista de argumentos.");

        return args;
    }

    public static ArrayList<ExprMetadata> parseArgsDeclaration(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos '(' para abrir a lista de argumentos.");

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return new ArrayList<>();
        }

        ArrayList<ExprMetadata> args = CallExpr.parseArgumentsDeclarationList(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' para fechar a lista de argumentos.");

        return args;
    }

    private String printArgs(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[");

        for (Expr entry : arguments)
        {
            ret.repeat("\t", next)
                    .append(entry.print(next))
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
                "\t".repeat(next) + "caller: " + caller.print(next) + ",\n" +
                "\t".repeat(next) + "args: " + printArgs(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
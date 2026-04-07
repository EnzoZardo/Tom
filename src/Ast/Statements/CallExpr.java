package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;

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
        args.add(AssignmentExpr.parse(parser));

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();
            args.add(AssignmentExpr.parse(parser));
        }

        return args;
    }

    public static ArrayList<Expr> parseArgs(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.expect(TokenType.OPEN_PARENTHESIS, "Expected open parenthesis");

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            return new ArrayList<>();
        }

        ArrayList<Expr> args = CallExpr.parseArgumentsList(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Missing close parenthesis in arguments list.");

        return args;
    }

    private String printArgs(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
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
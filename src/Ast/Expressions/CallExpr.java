package Ast.Expressions;

import Ast.Types.SymbolType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.Parser.ParsingException;
import Entities.Enums.Lexer.TokenType;
import Parser.Parser;
import Entities.Metadata.ExprMetadata;

import java.util.ArrayList;
import Entities.Abstractions.Type;

public class CallExpr extends Expr
{
    public final Expr caller;
    public ArrayList<Expr> arguments;
    public final ArrayList<Type> typeArguments;

    protected CallExpr(
        Expr caller,
        ArrayList<Expr> arguments,
        ArrayList<Type> typeArguments)
    {
        super(NodeType.CallExpression);
        this.arguments = arguments;
        this.caller = caller;
        this.typeArguments = typeArguments;
    }

    public static CallExpr create(
        Expr caller,
        ArrayList<Expr> arguments,
        ArrayList<Type> typeArguments)
    {
        return new CallExpr(caller, arguments, typeArguments);
    }

    public static Expr parse(Parser parser, Expr caller)
    {
        ArrayList<Type> typeArguments = new ArrayList<>();
        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
            typeArguments = SymbolType.parseArgs(parser);

        ArrayList<Expr> args = CallExpr.parseArgs(parser);

        Expr call = CallExpr.create(caller, args, typeArguments);

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            return CallExpr.parse(parser, call);

        return call;
    }

    public static ArrayList<Expr> parseArgumentsList(Parser parser)
    {
        ArrayList<Expr> args = new ArrayList<>();

        Expr first = Expr.parse(parser);

        args.add(first);

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();

            Expr arg = Expr.parse(parser);

            args.add(arg);
        }

        return args;
    }

    private static void parseArgDeclaration(ArrayList<ExprMetadata> args, Parser parser)
    {
        Expr identifier = AssignmentExpr.parse(parser);

        parser.expect(TokenType.COLON,
            "Esperávamos dois pontos - : - para o nome de um parâmetro de nossa função, mas recebemos " +
            "outro símbolo no código - %s");

        Type type = Type.parse(parser);

        args.add(ExprMetadata.create(type, identifier));
    }

    public static ArrayList<ExprMetadata> parseArgumentsDeclarationList(Parser parser)
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

    public static ArrayList<Expr> parseArgs(Parser parser)
    {
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir a lista de argumentos de uma função, mas recebemos outro símbolo no código - %s");

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return new ArrayList<>();
        }

        ArrayList<Expr> args = CallExpr.parseArgumentsList(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar a lista de argumentos de uma função, mas recebemos outro " +
            "símbolo no código - %s");

        return args;
    }

    public static ArrayList<ExprMetadata> parseArgsDeclaration(Parser parser)
    {
        parser.expect(TokenType.OPEN_PARENTHESIS, "Esperávamos um parênteses - ( - para " +
            "abrir a lista de parâmetros de uma função, mas recebemos outro símbolo no código - %s");

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return new ArrayList<>();
        }

        ArrayList<ExprMetadata> args = CallExpr.parseArgumentsDeclarationList(parser);

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de " +
            "parênteses - ) - para fechar a lista de parâmetros de uma função, mas recebemos outro " +
            "símbolo no código - %s");

        return args;
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

package Ast.Expressions.Literals;

import Ast.Expressions.CallExpr;
import Ast.Types.SymbolType;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Type;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;

public class ClassLiteral extends Expr
{
    public final ArrayList<Expr> arguments;
    public final String className;
    public final ArrayList<Type> typeArguments;

    protected ClassLiteral(ArrayList<Expr> arguments, String className, ArrayList<Type> typeArguments)
    {
        super(NodeType.ClassLiteral);
        this.arguments = arguments;
        this.className = className;
        this.typeArguments = typeArguments;
    }

    public static ClassLiteral create(ArrayList<Expr> args, String className)
    {
        return new ClassLiteral(args, className, new ArrayList<>());
    }

    public static ClassLiteral create(ArrayList<Expr> args, String className, ArrayList<Type> typeArguments)
    {
        return new ClassLiteral(args, className, typeArguments);
    }

    public static Expr parse(Parser parser)
    {
        parser.consume();

        Token name = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome da classe para" +
            "poder criar ela, mas recebemos outro símbolo no código - %s");

        ArrayList<Type> typeArguments = new ArrayList<>();

        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
            typeArguments = SymbolType.parseArgs(parser);

        ArrayList<Expr> args = CallExpr.parseArgs(parser);

        return ClassLiteral.create(args, name.value, typeArguments);
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

    private String printTypeArguments(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[");

        for (Type entry : typeArguments)
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
                "\t".repeat(next) + "class: " + className + ",\n" +
                "\t".repeat(next) + "typeArguments: " + printTypeArguments(next) + ",\n" +
                "\t".repeat(next) + "args: " + printArgs(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

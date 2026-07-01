package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.ParsingException;
import Parser.Parser;

public class Return extends Statement
{
    public final Expr value;
    private Return(Expr value)
    {
        super(NodeType.Return);
        this.value = value;
    }

    public static Return create(Expr value)
    {
        return new Return(value);
    }

    public static Return create()
    {
        return new Return(null);
    }

    public static Return parse(Parser parser)
    {
        if (parser.context.inFunction())
        {
            parser.consume();

            if (parser.peekIs(TokenType.INTERROGATION))
            {
                parser.consume();
                return Return.create();
            }

            Expr value = Expr.parse(parser);
            return Return.create(value);
        }

        throw new ParsingException("Só é possível usar um retorno dentro de uma função.");
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + "\n" +
                "\t".repeat(level) + "}";
    }
}

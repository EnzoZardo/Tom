package Ast.Statements;

import Ast.Expressions.PrimaryExpr;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidStatementContextException;
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

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        if (parser.context.inFunction())
        {
            parser.consume();
            return Return.create(Expr.parse(parser));
        }

        throw new InvalidStatementContextException("Só é possível usar um retorno dentro de uma função.");
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

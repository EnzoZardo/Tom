package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
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

    public static ErrorOr<Return> parse(Parser parser)
    {
        if (parser.context.inFunction())
        {
            parser.consume();
            ErrorOr<Expr> valueOr = Expr.parse(parser);
            if (valueOr.isError()) return valueOr.propagateError();
            return ErrorOr.Success(Return.create(valueOr.value));
        }

        return ErrorOr.Fail("Só é possível usar um retorno dentro de uma função.");
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

package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Enums.Ast.NodeType;
import Parser.Parser;

public class Break extends Statement
{
    private Break()
    {
        super(NodeType.Break);
    }

    public static Break create()
    {
        return new Break();
    }

    public static ErrorOr<Break> parse(Parser parser)
    {
        if (parser.context.inLoop())
        {
            parser.consume();
            return ErrorOr.Success(Break.create());
        }

        return ErrorOr.Fail(
            "Só é possível usar um pare dentro de um loop.",
            ErrorType.ParsingError,
            parser.peek().location);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }
}
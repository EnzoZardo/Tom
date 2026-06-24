package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Enums.Ast.NodeType;
import Parser.Parser;

public class Continue extends Statement
{
    private Continue()
    {
        super(NodeType.Continue);
    }

    public static Continue create()
    {
        return new Continue();
    }

    public static ErrorOr<Continue> parse(Parser parser)
    {
        if (parser.context.inLoop())
        {
            parser.consume();
            return ErrorOr.Success(Continue.create());
        }

        return ErrorOr.Fail(
            "Só é possível usar um continue dentro de um loop.",
            ErrorType.ParsingError,
            parser.peek().location);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + "\n" +
                "\t".repeat(level) + "}";
    }
}

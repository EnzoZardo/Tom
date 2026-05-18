package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.Parser.InvalidStatementContextException;
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

    public static Statement parse(Parser parser)
    {
        if (parser.context.inLoop())
        {
            parser.consume();
            return Continue.create();
        }

        throw new InvalidStatementContextException("Só é possível usar um continue dentro de um loop.");
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

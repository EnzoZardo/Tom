package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.Parser.InvalidStatementContextException;
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

    public static Statement parse(Parser parser)
    {
        if (parser.context.inLoop())
        {
            parser.consume();
            return Break.create();
        }

        throw new InvalidStatementContextException("Só é possível usar um pare dentro de um loop.");
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
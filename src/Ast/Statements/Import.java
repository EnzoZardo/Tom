package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Parser.Parser;

public class Import extends Statement
{
    public final Expr path;

    private Import(Expr path)
    {
        super(NodeType.Import);
        this.path = path;
    }

    public static Import create(Expr path)
    {
        return new Import(path);
    }

    public static ErrorOr<Import> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Expr> pathOr = Expr.parse(parser);
        if (pathOr.isError()) return pathOr.propagateError();
        return ErrorOr.Success(Import.create(pathOr.value));
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "path: " + path + "\n" +
                "\t".repeat(level) + "}";
    }
}


package Ast.Statements.Types;

import Ast.Enums.NodeType;
import Ast.Enums.TypeKind;
import Ast.Statements.Statement;
import Parser.Parser;

public abstract class Type extends Statement
{
    public TypeKind type;

    public Type(TypeKind type)
    {
        super(NodeType.TypeDeclaration);
        this.type = type;
    }

    public static Type parse(Parser parser)
    {
        return FunctionType.parse(parser);
    }

    public abstract String print(int level);

    @Override
    public String toString()
    {
        return this.print(0);
    }
}

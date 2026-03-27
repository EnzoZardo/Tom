package Ast.Types;

import Ast.Types.Enums.NodeType;

// Abstract Syntax Tree
public abstract class Statement
{
    public NodeType type;

    protected Statement(NodeType type)
    {
        this.type = type;
    }

    public abstract String print(int level);
}

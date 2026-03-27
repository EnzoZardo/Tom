package Ast.Statements;

import Ast.Types.Enums.NodeType;

import java.util.ArrayList;

public class ObjectLiteral extends Expr
{
    public ArrayList<Property> properties;

    protected ObjectLiteral(ArrayList<Property> properties)
    {
        super(NodeType.ObjectLiteral);
        this.properties = properties;
    }

    public static ObjectLiteral create(ArrayList<Property> properties)
    {
        return new ObjectLiteral(properties);
    }

    public static ObjectLiteral create()
    {
        return new ObjectLiteral(new ArrayList<>());
    }
}

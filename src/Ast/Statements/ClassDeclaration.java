package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Parser.Parser;

import java.util.ArrayList;

public class ClassDeclaration extends Statement
{
    public String name;
    public ArrayList<ClassMemberDeclaration> members;

    protected ClassDeclaration(
        String name,
        ArrayList<ClassMemberDeclaration> members
    )
    {
        super(NodeType.ClassDeclaration);
        this.name = name;
        this.members = members;
    }

    public static ClassDeclaration create(
        String name,
        ArrayList<ClassMemberDeclaration> members)
    {
        return new ClassDeclaration(name, members);
    }

    public static ClassDeclaration parse(Parser parser)
    {
        return null;
    }

    @Override
    public String print(int level)
    {
        return "";
    }
}

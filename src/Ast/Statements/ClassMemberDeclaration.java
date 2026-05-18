package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Parser.Parser;

public class ClassMemberDeclaration extends Statement
{
    public String protectionMarker;
    public Statement consequent;

    protected ClassMemberDeclaration(
        String protectionMarker,
        Statement consequent)
    {
        super(NodeType.ClassMemberDeclaration);
        this.protectionMarker = protectionMarker;
        this.consequent = consequent;
    }

    public static ClassMemberDeclaration create(
        String protectionMarker,
        Statement consequent)
    {
        return new ClassMemberDeclaration(protectionMarker, consequent);
    }

    public static ClassMemberDeclaration parse(Parser parser)
    {
        return null;
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
            "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "type: " + type.toString() + ",\n" +
            "\t".repeat(next) + "protectionMarker: " + protectionMarker + ",\n" +
            "\t".repeat(next) + "consequent: " + consequent.print(next) + "\n" +
            "\t".repeat(level) + "}";
    }
}

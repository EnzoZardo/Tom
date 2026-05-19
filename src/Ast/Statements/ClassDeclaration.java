package Ast.Statements;

import Ast.Expressions.Identifier;
import Entities.Abstractions.Ast.Statement;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Lexer.Tokens.Token;
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

    public static ClassDeclaration parse(Parser parser) throws InvalidArgumentException
    {
        parser.consume();

        Token identifier = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a classe declarada.");

        parser.expect(TokenType.OPEN_BRACE, "Esperávamos um '{' para a abertura de uma classe.");
        ArrayList<ClassMemberDeclaration> members = new ArrayList<>();
        parser.context.enterClass();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            members.add(ClassMemberDeclaration.parse(parser));
        }
        parser.context.outClass();
        parser.consume();

        return ClassDeclaration.create(identifier.value, members);
    }

    private String printBody(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[");

        int index = 0;
        for (ClassMemberDeclaration statement : members)
        {
            ret.repeat("\t", next).append(statement.print(next));

            if (index < members.size() - 1)
            {
                ret.append(", ");
            }

            index++;
        }

        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "type: " + type.toString() + ",\n" +
            "\t".repeat(next) + "name: " + name + ",\n" +
            "\t".repeat(next) + "body: " + printBody(next) + "\n" +
            "\t".repeat(level) + "}";
    }
}

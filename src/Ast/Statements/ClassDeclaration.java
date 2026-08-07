package Ast.Statements;

import Ast.Types.SymbolType;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.ParsingException;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;

public class ClassDeclaration extends Statement
{
    public String name;
    public String parentClass;
    public boolean isAbstract;
    public ArrayList<String> typeParameters;
    public ArrayList<ClassMemberDeclaration> members;

    protected ClassDeclaration(
        String name,
        ArrayList<ClassMemberDeclaration> members,
        String parentClass,
        ArrayList<String> typeParameters
    )
    {
        this.name = name;
        this.members = members;
        this.parentClass = parentClass;
        this.typeParameters = typeParameters;
        super(NodeType.ClassDeclaration);
    }

    public static ClassDeclaration create(
        String name,
        ArrayList<ClassMemberDeclaration> members,
        String parentClass,
        ArrayList<String> typeParameters)
    {
        return new ClassDeclaration(name, members, parentClass, typeParameters);
    }

    public static ClassDeclaration parse(Parser parser)
    {
        parser.consume();

        ArrayList<String> parameters = new ArrayList<>();
        Token identifier = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a classe " +
            "declarada, mas recebemos outro símbolo no código - %s");
        Token parentClassToken = null;

        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
        {
            //TODO: modularizar esse cara
            parser.consume();
            Token paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
            parameters.add(paramToken.value);
            while (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
                paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
                parameters.add(paramToken.value);
            }
            Token close = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos '>' para fechar a lista de parâmetros de tipo.");

            if (!ReservedKeys.Greater.equals(close.value))
                throw new ParsingException("Esperávamos '>' para fechar a lista de parâmetros de tipo.",
                    ErrorType.ParsingError, close.location);
        }

        if (parser.peekIs(TokenType.EXTENDS))
        {
            //TODO: aqui precisa de parse de tipo depois
            parser.consume();
            parentClassToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a classe que " +
                "será herdada, mas recebemos outro símbolo no código - %s");
        }

        parser.expect(TokenType.OPEN_BRACE, "Esperávamos uma abertura de chaves " +
            "- { - para a abertura do corpo da nossa classe " + identifier.value + ", mas recebemos outro " +
            "símbolo no código - %s");

        ArrayList<ClassMemberDeclaration> members = new ArrayList<>();

        parser.context.enterClass();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            members.add(ClassMemberDeclaration.parse(parser));
        }
        parser.context.outClass();

        parser.consume();
        return ClassDeclaration.create(
            identifier.value,
            members,
            parentClassToken == null ? null : parentClassToken.value,
            parameters);
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
            if (index < members.size() - 1) ret.append(", ");
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

package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Lexer.Tokens.Token;
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

    public static ClassMemberDeclaration parse(Parser parser) throws InvalidArgumentException
    {
        // TODO: create constructor logic
        Token protectionMarker = parser.expect(TokenType.PROTECTION_MARKER, "Esperávamos um nível de proteção "
            + "para o membro da classe declarada.");

        if (!parser.peekIs(TokenType.DECLARE) &&
            !parser.peekIs(TokenType.CONSTANT) &&
            !parser.peekIs(TokenType.FUNCTION) &&
            !parser.peekIs(TokenType.CLASS))
        // TODO: && is not the class name, that will be passed with the parameters
        {
            throw new InvalidTokenException("Declaração inválida de membro de classe.");
        }

        Statement consequent = Statement.parse(parser);

        return ClassMemberDeclaration.create(protectionMarker.value, consequent);
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

package Ast.Statements;

import Entities.Abstractions.Ast.Statement;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Lexer.Tokens.Token;
import Parser.Parser;

public class ClassMemberDeclaration extends Statement
{
    public ProtectionLevel protectionMarker;
    public Statement consequent;

    protected ClassMemberDeclaration(
        ProtectionLevel protectionMarker,
        Statement consequent)
    {
        super(NodeType.ClassMemberDeclaration);
        this.protectionMarker = protectionMarker;
        this.consequent = consequent;
    }

    public static ClassMemberDeclaration create(
        ProtectionLevel protectionMarker,
        Statement consequent)
    {
        return new ClassMemberDeclaration(protectionMarker, consequent);
    }

    public static ProtectionLevel getProtectionLevel(String protectionMarker) {
        return switch (protectionMarker) {
            case ReservedKeys.Protected -> ProtectionLevel.Protected;
            case ReservedKeys.Private -> ProtectionLevel.Private;
            case ReservedKeys.Public -> ProtectionLevel.Public;
            default -> throw new InvalidTokenException("Marcador de nível de proteção inválido: " + protectionMarker);
        };
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

        return ClassMemberDeclaration.create(
            getProtectionLevel(protectionMarker.value),
            consequent);
    }

    public String getMemberName()
    {
        return switch (consequent.type) {
            case NodeType.VariableDeclaration -> ((VariableDeclaration) consequent).identifier;
            case NodeType.FunctionDeclaration -> ((FunctionDeclaration) consequent).identifier;
            case NodeType.ClassDeclaration -> ((ClassDeclaration) consequent).name;
            default -> throw new InvalidTokenException("Declaração inválida de membro de classe.");
        };
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

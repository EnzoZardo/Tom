package Ast.Statements;

import Ast.Expressions.Identifier;
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
    public final ProtectionLevel protectionMarker;
    public final Statement consequent;
    public final boolean isStatic;

    protected ClassMemberDeclaration(
        ProtectionLevel protectionMarker,
        Statement consequent,
        boolean isStatic)
    {
        super(NodeType.ClassMemberDeclaration);
        this.protectionMarker = protectionMarker;
        this.consequent = consequent;
        this.isStatic = isStatic;
    }

    public static ClassMemberDeclaration create(
        ProtectionLevel protectionMarker,
        Statement consequent,
        boolean isStatic)
    {
        return new ClassMemberDeclaration(protectionMarker, consequent, isStatic);
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
        Token protectionMarker = parser.expect(TokenType.PROTECTION_MARKER, "Esperávamos um nível de proteção "
            + "para o membro da classe declarada.");

        boolean isStatic = false;

        if (parser.peekIs(TokenType.STATIC_MARKER))
        {
            parser.consume();
            isStatic = true;
        }

        if (!parser.peekIs(TokenType.DECLARE) &&
            !parser.peekIs(TokenType.CONSTANT) &&
            !parser.peekIs(TokenType.FUNCTION) &&
            !parser.peekIs(TokenType.CLASS))
        {
            throw new InvalidTokenException("Declaração inválida de membro de classe.");
        }

        Statement consequent = Statement.parse(parser);

        return ClassMemberDeclaration.create(
            getProtectionLevel(protectionMarker.value),
            consequent,
            isStatic);
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

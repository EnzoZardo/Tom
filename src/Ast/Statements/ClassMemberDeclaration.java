package Ast.Statements;

import Ast.Types.FunctionType;
import Entities.Abstractions.Ast.Statement;
import Entities.Abstractions.Type;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Exceptions.Parser.ParsingException;
import Entities.Metadata.ArgumentMetadata;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public static ProtectionLevel getProtectionLevel(String protectionMarker)
    {
        return switch (protectionMarker)
        {
            case ReservedKeys.Protected -> ProtectionLevel.Protected;
            case ReservedKeys.Private -> ProtectionLevel.Private;
            case ReservedKeys.Public -> ProtectionLevel.Public;
            default -> throw new ParsingException("Marcador de nível de proteção inválido: " + protectionMarker);
        };
    }

    public String getMemberName()
    {
        return switch (consequent.type)
        {
            case VariableDeclaration -> ((VariableDeclaration) consequent).identifier;
            case FunctionDeclaration -> ((FunctionDeclaration) consequent).identifier;
            default -> null;
        };
    }

    public Type getMemberType()
    {
        return switch (consequent.type)
        {
            case VariableDeclaration ->
            {
                VariableDeclaration declaration = (VariableDeclaration) consequent;
                yield declaration.expectedType;
            }
            case FunctionDeclaration ->
            {
                FunctionDeclaration declaration = (FunctionDeclaration) consequent;
                yield FunctionType.create(
                    declaration.parameters.stream()
                        .map(ArgumentMetadata::getType)
                        .collect(Collectors.toCollection(ArrayList::new)),
                    declaration.returnType);
            }
            default -> null;
        };
    }

    public static ClassMemberDeclaration parse(Parser parser)
    {
        Token protectionMarker = parser.expect(TokenType.PROTECTION_MARKER, "Esperávamos um nível " +
                "de proteção (público, privado ou protegido) para o membro da classe declarada, mas recebemos outro " +
                "símbolo no código - %s");

        boolean isStatic = false;

        if (parser.peekIs(TokenType.STATIC_MARKER))
        {
            parser.consume();
            isStatic = true;
        }

        if (!parser.peekIs(TokenType.DECLARE) &&
            !parser.peekIs(TokenType.CONSTANT) &&
            !parser.peekIs(TokenType.FUNCTION))
            throw new ParsingException(
                "Declaração inválida de membro de classe, só são aceitas funções ou declarações de variáveis.");

        Statement consequent = Statement.parse(parser);

        ProtectionLevel level = getProtectionLevel(protectionMarker.value);

        return ClassMemberDeclaration.create(
            level,
            consequent,
            isStatic);
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

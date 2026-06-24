package Ast.Statements;

import Ast.Types.FunctionType;
import Entities.Abstractions.Ast.Statement;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Lexer.TokenType;
import Entities.Enums.Runtime.ProtectionLevel;
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

    public static ErrorOr<ProtectionLevel> getProtectionLevel(String protectionMarker)
    {
        return switch (protectionMarker)
        {
            case ReservedKeys.Protected -> ErrorOr.Success(ProtectionLevel.Protected);
            case ReservedKeys.Private -> ErrorOr.Success(ProtectionLevel.Private);
            case ReservedKeys.Public -> ErrorOr.Success(ProtectionLevel.Public);
            default -> ErrorOr.Fail("Marcador de nível de proteção inválido: " + protectionMarker);
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

    public static ErrorOr<ClassMemberDeclaration> parse(Parser parser)
    {
        ErrorOr<Token> protectionMarkerOr = parser.expect(TokenType.PROTECTION_MARKER, "Esperávamos um nível " +
                "de proteção (público, privado ou protegido) para o membro da classe declarada, mas recebemos outro " +
                "símbolo no código - %s");
        if (protectionMarkerOr.isError()) return protectionMarkerOr.propagateError();

        boolean isStatic = false;

        if (parser.peekIs(TokenType.STATIC_MARKER))
        {
            parser.consume();
            isStatic = true;
        }

        if (!parser.peekIs(TokenType.DECLARE) &&
            !parser.peekIs(TokenType.CONSTANT) &&
            !parser.peekIs(TokenType.FUNCTION))
            return ErrorOr.Fail(
                "Declaração inválida de membro de classe, só são aceitas funções ou declarações de variáveis.",
                ErrorType.ParsingError,
                parser.peek().location);

        var consequentOr = Statement.parse(parser);
        if (consequentOr.isError()) return consequentOr.propagateError();

        ErrorOr<ProtectionLevel> levelOr = getProtectionLevel(protectionMarkerOr.value.value);
        if (levelOr.isError()) return levelOr.propagateError();

        return ErrorOr.Success(ClassMemberDeclaration.create(
            levelOr.value,
            consequentOr.value,
            isStatic));
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

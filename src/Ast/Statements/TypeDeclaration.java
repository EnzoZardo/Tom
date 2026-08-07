package Ast.Statements;

import Ast.Expressions.Identifier;
import Ast.Types.SymbolType;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.ParsingException;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;

public class TypeDeclaration extends Statement
{
    public Type value;
    public String identifier;
    public ArrayList<String> typeParameters;

    protected TypeDeclaration(
        Type value,
        String identifier,
        ArrayList<String> typeParameters)
    {
        super(NodeType.TypeDeclaration);
        this.value = value;
        this.identifier = identifier;
        this.typeParameters = typeParameters;
    }

    public static TypeDeclaration create(
        Type value,
        String identifier,
        ArrayList<String> typeParameters)
    {
        return new TypeDeclaration(value, identifier, typeParameters);
    }

    public static TypeDeclaration parse(Parser parser)
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do tipo em sua declaração.");
        String identifier = identifierToken.value;

        ArrayList<String> typeParameters = new ArrayList<>();
        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
        {
            parser.consume();
            Token paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
            typeParameters.add(paramToken.value);
            while (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
                paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
                typeParameters.add(paramToken.value);
            }
            Token close = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos '>' para fechar a lista de parâmetros de tipo.");
            if (!ReservedKeys.Greater.equals(close.value))
                throw new ParsingException("Esperávamos '>' para fechar a lista de parâmetros de tipo.", ErrorType.ParsingError, close.location);
        }

        parser.expect(TokenType.EQUALS, "Esperávamos '=' para declararmos o tipo " + identifier + ".");
        Type type = Type.parse(parser);
        return TypeDeclaration.create(type, identifier, typeParameters);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "typeParameters: " + typeParameters + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + "\n" +
                "\t".repeat(level) + "}";
    }
}

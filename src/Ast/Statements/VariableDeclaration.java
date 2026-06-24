package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class VariableDeclaration extends Statement
{
    public Type expectedType;
    public Expr value;
    public boolean constant;
    public String identifier;

    protected VariableDeclaration(
            Expr value,
            Type expectedType,
            String identifier,
            boolean constant)
    {
        super(NodeType.VariableDeclaration);
        this.expectedType = expectedType;
        this.value = value;
        this.constant = constant;
        this.identifier = identifier;
    }

    public static VariableDeclaration create(
        Expr value,
        Type type,
        String identifier,
        boolean constant)
    {
        return new VariableDeclaration(value, type, identifier, constant);
    }

    public static VariableDeclaration create(String identifier, Type type)
    {
        return new VariableDeclaration(null, type, identifier, false);
    }

    public static ErrorOr<VariableDeclaration> parse(Parser parser)
    {
        boolean isConstant = parser.consume().type == TokenType.CONSTANT;
        ErrorOr<Token> identifierTokenOr = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome da variável em sua declaração.");
        if (identifierTokenOr.isError()) return identifierTokenOr.propagateError();
        String identifier = identifierTokenOr.value.value;

        ErrorOr<Token> colonOr = parser.expect(TokenType.COLON, "Esperávamos ':' depois do nome de uma variável para conseguirmos o seu tipo.");
        if (colonOr.isError()) return colonOr.propagateError();
        ErrorOr<Type> typeOr = Type.parse(parser);
        if (typeOr.isError()) return typeOr.propagateError();
        Type type = typeOr.value;

        if (parser.peekIs(TokenType.INTERROGATION))
        {
            parser.consume();
            if (isConstant)
                return ErrorOr.Fail("Não podemos declarar uma constante sem valor.");
            return ErrorOr.Success(VariableDeclaration.create(identifier, type));
        }

        ErrorOr<Token> equalsOr = parser.expect(TokenType.EQUALS, "Esperávamos '=' para recebermos o valor da variável " + identifier + ".");
        if (equalsOr.isError()) return equalsOr.propagateError();
        ErrorOr<Expr> valueOr = Expr.parse(parser);
        if (valueOr.isError()) return valueOr.propagateError();
        return ErrorOr.Success(VariableDeclaration.create(valueOr.value, type, identifier, isConstant));
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "type: " + type.toString() + ",\n" +
            "\t".repeat(next) + "value: " + (value == null ?  ReservedKeys.Null : value.print(next)) + ",\n" +
            "\t".repeat(next) + "constant: " + constant + ",\n" +
            "\t".repeat(next) + "identifier: " + identifier + ",\n" +
            "\t".repeat(next) + "expectedType: " + expectedType.print(next) + "\n" +
            "\t".repeat(level) + "}";
    }
}

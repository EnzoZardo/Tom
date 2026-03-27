package Parser;

import Ast.Statements.*;
import Ast.Types.Statement;
import Constants.ReservedKeys;
import Exceptions.AlreadyParsedException;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Exceptions.NullConstantException;
import Lexer.Lexer;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

import java.util.ArrayList;

public class Parser
{
    private final ArrayList<Token> tokens;
    private int tokenIndex = 0;

    private Parser(char[] content) throws AlreadyParsedException, InvalidTokenException
    {
        Lexer lexer = Lexer.create(content);
        tokens = lexer.tokenize();
    }

    public static Parser create(char[] content) throws AlreadyParsedException, InvalidTokenException
    {
        return new Parser(content);
    }

    public Program build() throws InvalidArgumentException, InvalidTokenException
    {

        Program program = Program.create();
        while (_notEof())
        {
            program.addStatement(_parseStatement());
        }

        return program;
    }

    private Statement _parseStatement() throws InvalidArgumentException, InvalidTokenException
    {
        return switch (_peek().type)
        {
            case TokenType.DECLARE,
                 TokenType.CONSTANT -> _parseVariableDeclaration();
            default -> _parseExpr();
        };
    }

    private Expr _parseVariableDeclaration() throws InvalidTokenException, InvalidArgumentException
    {
        boolean isConstant = _consume().type == TokenType.CONSTANT;
        Token identifierToken = _expect(TokenType.IDENTIFIER, "Expecting identifier name following let/const.");
        String identifier = identifierToken.value;

        if (_peekIs(TokenType.SEMICOLON))
        {
            _consume();
            NullConstantException.ThrowIf(isConstant);
            return VariableDeclaration.notInstanced(identifier);
        }
        _expect(TokenType.EQUALS, "Expecting equals token to declare a variable.");
        return VariableDeclaration.create(_parseExpr(), identifier, isConstant);
    }

    private Expr _parseObjectExpr() throws InvalidTokenException, InvalidArgumentException
    {
        if (!_peekIs(TokenType.OPEN_BRACE))
        {
            return _parseAdditiveExpr();
        }
        _consume();

        ArrayList<Property> properties = new ArrayList<>();
        while (_notEof() && !_peekIs(TokenType.CLOSE_BRACE))
        {
            Token key = _expect(TokenType.IDENTIFIER, "Expecting identifier as object key.");

            if (_peekIs(TokenType.COMMA))
            {
                _consume();
                properties.add(Property.create(key.value));
                continue;
            }

            if (_peekIs(TokenType.CLOSE_BRACE))
            {
                properties.add(Property.create(key.value));
                continue;
            }

            _expect(TokenType.COLON, "Expecting colon after object key.");
            Expr value = _parseExpr();

            if (!_peekIs(TokenType.CLOSE_BRACE))
            {
                _expect(TokenType.COMMA, "Invalid token found parsing object. Expected comma or close brace.");
            }

            if (_peekIs(TokenType.COMMA))
            {
                _consume();
            }

            properties.add(Property.create(key.value, value));
        }

        _expect(TokenType.CLOSE_BRACE, "Expecting a close brace after last object value.");
        return ObjectLiteral.create(properties);
    }

    private Expr _parseAssignmentExpr() throws InvalidTokenException, InvalidArgumentException
    {
        Expr left = _parseObjectExpr();

        if (_peekIs(TokenType.EQUALS))
        {
            _consume();
            Expr value = _parseAssignmentExpr();
            return AssignmentExpr.create(left, value);
        }

        return left;
    }

    private Expr _parseExpr() throws InvalidArgumentException, InvalidTokenException
    {
        return _parseAssignmentExpr();
    }

    private Expr _parseAdditiveExpr() throws InvalidArgumentException, InvalidTokenException
    {
        Expr left = _parseMultiplicativeExpr();

        while (ReservedKeys.Plus == _peek().Char()
                || ReservedKeys.Minus == _peek().Char())
        {
            String operator = _consume().value;
            Expr right = _parseMultiplicativeExpr();
            left = BinaryExpr.create(left, right, operator);
        }

        return left;
    }

    private Expr _parseMultiplicativeExpr() throws InvalidArgumentException, InvalidTokenException
    {
        Expr left = _parsePrimaryExpr();

        while (ReservedKeys.Multiplication == _peek().Char()
                || ReservedKeys.Division == _peek().Char()
                || ReservedKeys.Mod == _peek().Char())
        {
            String operator = _consume().value;
            Expr right = _parsePrimaryExpr();
            left = BinaryExpr.create(left, right, operator);
        }

        return left;
    }

    private Token _expect(TokenType token, String error)
    {
        Token prev = _consume();
        if (prev.type != token)
        {
            System.err.println(error);
            System.exit(1);
        }
        return prev;
    }

    private Expr _parseParenthesisExpr() throws InvalidTokenException, InvalidArgumentException
    {
        _consume();
        Expr expr = _parseExpr();
        _expect(TokenType.CLOSE_PARENTHESIS, String.format("Unexpected token received. Expected ')', received '%s'", _peek().value));
        return expr;
    }

    private Expr _parsePrimaryExpr() throws InvalidArgumentException, InvalidTokenException
    {
        return switch (_peek().type)
        {
            case TokenType.IDENTIFIER -> Identifier.create(_consume());
            case TokenType.NUMERIC -> NumericLiteral.create(_consume());
            case TokenType.OPEN_PARENTHESIS -> _parseParenthesisExpr();
            default -> throw new InvalidTokenException(String.format("Unexpected Token %s.", _peek().value));
        };
    }

    private boolean _notEof()
    {
        return _peek().type != TokenType.EOF;
    }

    private Token _peek()
    {
        return _peek(0);
    }

    private boolean _peekIs(TokenType token)
    {
        return _peek().type == token;
    }

    private Token _peek(int offset)
    {
        if (tokenIndex >= tokens.size())
        {
            return null;
        }
        return tokens.get(tokenIndex + offset);
    }

    private Token _consume()
    {
        return tokens.get(tokenIndex++);
    }
}

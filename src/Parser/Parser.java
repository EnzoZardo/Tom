package Parser;

import Ast.Statements.*;
import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Constants.ReservedKeys;
import Exceptions.*;
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
//            IO.println(program.body.getLast().print(0));
        }

        return program;
    }

    private Statement _parseStatement() throws InvalidArgumentException, InvalidTokenException
    {
        return switch (_peek().type)
        {
            case TokenType.DECLARE,
                 TokenType.CONSTANT -> _parseVariableDeclaration();
            case TokenType.FUNCTION -> _parseFunctionDeclaration();
            default -> _parseExpr();
        };
    }

    private Statement _parseVariableDeclaration() throws InvalidTokenException, InvalidArgumentException
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

    private Statement _parseFunctionDeclaration() throws InvalidTokenException, InvalidArgumentException
    {
        _consume();
        Token identifierToken = _expect(TokenType.IDENTIFIER, "Expecting identifier name following function.");
        String name = identifierToken.value;

        ArrayList<Expr> parametersIdentifiers = _parseArgs();
        ArrayList<String> parameters = new ArrayList<>();

        for (Expr identifier : parametersIdentifiers)
        {
            if (identifier.type != NodeType.Identifier)
            {
                throw new InvalidNodeException("Expected parameter identifier in parameters list.");
            }

            parameters.add(((Identifier) identifier).get());
        }

        _expect(TokenType.OPEN_BRACE, "Expecting '{' after function arguments declaration.");
        ArrayList<Statement> body = new ArrayList<>();

        while (_notEof() && !_peekIs(TokenType.CLOSE_BRACE)) {
            body.add(_parseStatement());
        }

        _expect(TokenType.CLOSE_BRACE, "Expecting '}' after function body declaration.");

        return FunctionDeclaration.create(name, parameters, body);
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

    public Expr _parseCallMemberExpr() throws InvalidTokenException, InvalidArgumentException
    {
        Expr member = _parseMemberExpr();

        if (_peekIs(TokenType.OPEN_PARENTHESIS)) {
            return _parseCallExpr(member);
        }

        return member;
    }

    public Expr _parseMemberExpr() throws InvalidTokenException, InvalidArgumentException
    {
        Expr object = _parsePrimaryExpr();

        while (_peekIs(TokenType.DOT) || _peekIs(TokenType.OPEN_BRACKETS))
        {
            Token operator = _consume();
            boolean computed;
            Expr property;

            if (operator.type == TokenType.DOT) {
                computed = false;
                property = _parsePrimaryExpr();

                if (property.type != NodeType.Identifier)
                {
                    throw new InvalidNodeException("Expected an identifier after dot on member expression.");
                }
            }
            else
            {
                computed = true;
                property = _parsePrimaryExpr();
                _expect(TokenType.CLOSE_BRACKETS, "Expecting close brackets after computed member expression");
            }

            object = MemberExpr.create(object, property, computed);
        }

        return object;
    }

    public Expr _parseCallExpr(Expr caller) throws InvalidTokenException, InvalidArgumentException
    {
        Expr call = CallExpr.create(caller, _parseArgs());

        if (_peekIs(TokenType.OPEN_PARENTHESIS))
        {
            call = _parseCallExpr(call);
        }

        return call;
    }

    public ArrayList<Expr> _parseArgumentsList() throws InvalidTokenException, InvalidArgumentException
    {
        ArrayList<Expr> args = new ArrayList<>();
        args.add(_parseAssignmentExpr());

        while (_notEof() && _peekIs(TokenType.COMMA))
        {
            _consume();
            args.add(_parseAssignmentExpr());
        }

        return args;
    }

    public ArrayList<Expr> _parseArgs() throws InvalidTokenException, InvalidArgumentException
    {
        _expect(TokenType.OPEN_PARENTHESIS, "Expected open parenthesis");

        if (_peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            return new ArrayList<>();
        }

        ArrayList<Expr> args = _parseArgumentsList();

        _expect(TokenType.CLOSE_PARENTHESIS, "Missing close parenthesis in arguments list.");

        return args;
    }

    private Expr _parseMultiplicativeExpr() throws InvalidArgumentException, InvalidTokenException
    {
        Expr left = _parseCallMemberExpr();

        while (ReservedKeys.Multiplication == _peek().Char()
                || ReservedKeys.Division == _peek().Char()
                || ReservedKeys.Mod == _peek().Char())
        {
            String operator = _consume().value;
            Expr right = _parseCallMemberExpr();
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
            System.err.println(prev);
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

    /// {@summary}
    /// Faz o parse das expressões primárias na precedência:
    /// - Assignment
    /// - Object
    /// - Additive
    /// - Multiplicative
    /// - Call
    /// - Member
    /// - Primary
    private Expr _parsePrimaryExpr() throws InvalidArgumentException, InvalidTokenException
    {
        return switch (_peek().type)
        {
            case TokenType.IDENTIFIER -> Identifier.create(_consume());
            case TokenType.NUMERIC -> NumericLiteral.create(_consume());
            case TokenType.OPEN_PARENTHESIS -> _parseParenthesisExpr();
            default -> throw new InvalidTokenException(String.format("Unexpected Token '%s'.", _peek().value));
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

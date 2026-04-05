package Lexer;

import Constants.ReservedOperators;
import Constants.ReservedWords;
import Exceptions.AlreadyParsedException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.PonctuationToken;
import Lexer.Types.Token;

import java.util.ArrayList;

public class Lexer
{
    private int tokenIndex;
    private final char[] content;
    private final ArrayList<Token> tokens;

    private Lexer(char[] content)
    {
        this.content = content;
        this.tokens = new ArrayList<>();
        this.tokenIndex = 0;
    }

    public static Lexer create(char[] content)
    {
        return new Lexer(content);
    }

    public ArrayList<Token> tokenize() throws AlreadyParsedException, InvalidTokenException
    {
        if (tokenIndex != 0)
        {
            throw new AlreadyParsedException("Content was already tokenized. Consult 'getTokens' method.");
        }

        while (_peek() != null)
        {
            char current = _peek();
            if (PonctuationToken.isOpenParenthesis(current))
            {
                _consumeAndAdd(TokenType.OPEN_PARENTHESIS, current);
            }
            else if (PonctuationToken.isCloseParenthesis(current))
            {
                _consumeAndAdd(TokenType.CLOSE_PARENTHESIS, current);
            }
            else if (PonctuationToken.isOpenBrace(current))
            {
                _consumeAndAdd(TokenType.OPEN_BRACE, current);
            }
            else if (PonctuationToken.isCloseBrace(current))
            {
                _consumeAndAdd(TokenType.CLOSE_BRACE, current);
            }
            else if (PonctuationToken.isOpenBrackets(current))
            {
                _consumeAndAdd(TokenType.OPEN_BRACKETS, current);
            }
            else if (PonctuationToken.isCloseBrackets(current))
            {
                _consumeAndAdd(TokenType.CLOSE_BRACKETS, current);
            }
            else if (PonctuationToken.isDot(current))
            {
                _consumeAndAdd(TokenType.DOT, current);
            }
            else if (PonctuationToken.isQuotationMark(current))
            {
                _string();
            }
            else if (ReservedOperators.isReserved(Character.toString(current)) || Token.isAlphabeticOperator(current))
            {
                _operator(_consume());
            }
            else if (Token.isEquals(current))
            {
                _consumeAndAdd(TokenType.EQUALS, current);
            }
            else if (PonctuationToken.isSemicolon(current))
            {
                _consumeAndAdd(TokenType.SEMICOLON, current);
            }
            else if (PonctuationToken.isColon(current))
            {
                _consumeAndAdd(TokenType.COLON, current);
            }
            else if (PonctuationToken.isComma(current))
            {
                _consumeAndAdd(TokenType.COMMA, current);
            }
            else
            {
                if (Token.isAlphabetic(current))
                {
                    _alphabetic(_consume());
                }
                else if (Token.isNumeric(current))
                {
                    _numeric(_consume());
                }
                else if (Token.isIgnorable(current))
                {
                    _consume();
                }
                else
                {
                    System.err.printf("Unexpected Token" + _peek() + ".");
                    System.exit(1);
                    //Decidir entre o de cima ou esse: throw new InvalidTokenException("Unrecognizable token found in source code.");
                }
            }

        }
        _eof();
        return tokens;
    }

    private void _eof()
    {
        tokens.add(Token.create(TokenType.EOF, ""));
    }

    private void _operator(char c)
    {
        StringBuilder token = new StringBuilder(Character.toString(c));

        if (Character.isAlphabetic(c))
        {
            while (_peek() != null && Character.isAlphabetic(_peek()))
            {
                token.append(_consume());
            }

            String tk = token.toString();
            if (ReservedWords.isReserved(tk))
            {
                tokens.add(ReservedWords.token(tk));
                return;
            }

            tokens.add(Token.create(TokenType.IDENTIFIER, token.toString()));
            return;
        }

        while (_peek() != null
            && ReservedOperators.isReserved(token.toString())
            && ReservedOperators.isReserved(Character.toString(_peek())))
        {
            token.append(_consume());
        }

        tokens.add(ReservedOperators.token(token.toString()));
    }

    private void _string()
    {
        _consume();
        StringBuilder token = new StringBuilder();
        while (_peek() != null && !PonctuationToken.isQuotationMark(_peek()))
        {
            if (PonctuationToken.isBackslash(_peek()))
            {
                token.append(_stringEscape());
                continue;
            }
            token.append(_consume());
        }
        _consume();
        tokens.add(Token.create(TokenType.STRING_LITERAL, token.toString()));
    }

    private String _stringEscape()
    {
        String backslash = Character.toString(_consume());

        return switch (_consume())
        {
            case 'n' -> "\n";
            case 't' -> "\t";
            case 'r' -> "\r";
            case 'b' -> "\b";
            case 'f' -> "\f";
            case '\\' -> "\\";
            case '\'' -> "'";
            case '\"' -> "\"";
            default -> backslash + _peek();
        };
    }

    private void _alphabetic(char c)
    {
        StringBuilder token = new StringBuilder(Character.toString(c));
        while (_peek() != null && Character.isAlphabetic(_peek()))
        {
            token.append(_consume());
        }

        if (ReservedWords.isReserved(token.toString()))
        {
            tokens.add(ReservedWords.token(token.toString()));
        }
        else
        {
            tokens.add(Token.create(TokenType.IDENTIFIER, token.toString()));
        }
    }

    private void _numeric(char c)
    {
        StringBuilder token = new StringBuilder(Character.toString(c));
        TokenType type = TokenType.INTEGER_LITERAL;

        while (_peek() != null && (Character.isDigit(_peek()) || PonctuationToken.isDot(_peek())))
        {
            if (PonctuationToken.isDot(_peek()) && type != TokenType.FLOAT_LITERAL)
            {
                type = TokenType.FLOAT_LITERAL;
            }
            else if (PonctuationToken.isDot(_peek()))
            {
                break;
            }

            token.append(_consume());
        }

        tokens.add(Token.create(type, token.toString()));
    }

    private void _consumeAndAdd(TokenType type, String value)
    {
        _consume();
        tokens.add(Token.create(type, value));
    }

    private void _consumeAndAdd(TokenType type, char value)
    {
        _consume();
        tokens.add(Token.create(type, Character.toString(value)));
    }

    private Character _peek()
    {
        return _peek(0);
    }

    private Character _peek(int offset)
    {
        if (tokenIndex >= content.length)
        {
            return null;
        }
        return content[tokenIndex + offset];
    }

    private Character _consume()
    {
        return content[tokenIndex++];
    }
}

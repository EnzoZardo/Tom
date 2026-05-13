package Lexer;

import Entities.Constants.ReservedComments;
import Entities.Constants.ReservedOperators;
import Entities.Constants.ReservedWords;
import Entities.Exceptions.Parser.AlreadyParsedException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.PonctuationToken;
import Lexer.Tokens.Token;
import Lexer.Tokens.TokenFileLocation;
import Lexer.Tokens.TokenLocation;

import java.util.ArrayList;

public class Lexer
{
    private int columnIndex;
    private int lineIndex;
    private int tokenIndex;
    private final char[] content;
    private final ArrayList<Token> tokens;

    private Lexer(char[] content)
    {
        this.content = content;
        this.tokens = new ArrayList<>();
        this.lineIndex = 1;
        this.tokenIndex = 0;
        this.columnIndex = 0;
    }

    public static Lexer create(char[] content)
    {
        return new Lexer(content);
    }

    public ArrayList<Token> tokenize() throws InvalidTokenException
    {
        if (tokenIndex != 0)
        {
            throw new AlreadyParsedException("Conteúdo já foi transformado em símbolo.");
        }

        while (_peek() != null)
        {
            char current = _peek();
            if (ReservedComments.isInlineComment(current))
            {
                _inlineComment();
            }
            else if (ReservedComments.isOpenMultiLineComment(current, _peek(1)))
            {
                _multiLineComment();
            }
            else if (PonctuationToken.isOpenParenthesis(current))
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
            else if (ReservedOperators.isReserved(Character.toString(current))
                || Token.isAlphabeticOperator(current))
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
                else if (Token.isNewLine(current))
                {
                    _nextLine();
                    _consume();
                }
                else if (Token.isIgnorable(current))
                {
                    _consume();
                }
                else
                {
                    System.err.printf("Símbolo inesperado " + _peek() + ".");
                    System.exit(1);
                }
            }

        }
        _eof();
        return tokens;
    }

    private void _nextLine() {
        columnIndex = 0;
        lineIndex++;
    }

    private void _eof()
    {
        TokenFileLocation fileLocation = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        TokenLocation location = TokenLocation.create(fileLocation, fileLocation);
        tokens.add(Token.create(TokenType.EOF, "", location));
    }

    private void _operator(char c)
    {
        StringBuilder token = new StringBuilder(Character.toString(c));
        TokenFileLocation start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);

        if (Character.isAlphabetic(c))
        {
            while (_peek() != null && Character.isAlphabetic(_peek()))
            {
                token.append(_consume());
            }

            String tk = token.toString();
            if (ReservedWords.isReserved(tk))
            {
                TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
                tokens.add(ReservedWords.token(tk, start, end));
                return;
            }

            TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
            tokens.add(Token.create(TokenType.IDENTIFIER, token.toString(), start, end));
            return;
        }

        while (_peek() != null
            && ReservedOperators.isReserved(token.toString())
            && ReservedOperators.isReserved(Character.toString(_peek())))
        {
            if (start == null)
            {
                start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
            }

            if (!ReservedOperators.isReserved(token + Character.toString(_peek())))
            {
                TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
                tokens.add(ReservedOperators.token(token.toString(), start, end));
                start = null;
                token = new StringBuilder(Character.toString(_consume()));
                continue;
            }

            token.append(_consume());
        }

        TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        tokens.add(ReservedOperators.token(token.toString(), start, end));
    }

    private void _inlineComment()
    {
        do
        {
            _consume();
        } while (_peek() != null && _peek() != '\n');
    }

    private void _multiLineComment()
    {
        do
        {
            if (Token.isNewLine(_consume()))
            {
                _nextLine();
            }
        } while (_peek() != null && !ReservedComments.isCloseMultiLineComment(_peek(), _peek(1)));
        _consume();
        _consume();
    }

    private void _string()
    {
        TokenFileLocation start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        _consume();
        StringBuilder token = new StringBuilder();
        while (_peek() != null && !PonctuationToken.isQuotationMark(_peek()))
        {
            if (PonctuationToken.isBackslash(_peek()))
            {
                String escape = _stringEscape();
                token.append(escape);
                if (Token.isNewLine(escape))
                {
                    _nextLine();
                }
                continue;
            }
            token.append(_consume());
        }

        TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        _consume();
        tokens.add(Token.create(TokenType.STRING_LITERAL, token.toString(), start, end));
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
        TokenFileLocation start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        while (_peek() != null && Character.isAlphabetic(_peek()))
        {
            token.append(_consume());
        }

        TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        if (ReservedWords.isReserved(token.toString()))
        {
            tokens.add(ReservedWords.token(token.toString(), start, end));
        }
        else
        {
            tokens.add(Token.create(TokenType.IDENTIFIER, token.toString(), start, end));
        }
    }

    private void _numeric(char c)
    {
        TokenFileLocation start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
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

        TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        tokens.add(Token.create(type, token.toString(), start, end));
    }

    private void _consumeAndAdd(TokenType type, char value)
    {
        TokenFileLocation start = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        _consume();
        TokenFileLocation end = TokenFileLocation.create(columnIndex, lineIndex, tokenIndex);
        tokens.add(Token.create(type, Character.toString(value), start, end));
    }

    private Character _peek()
    {
        return _peek(0);
    }

    private Character _peek(int offset)
    {
        if (tokenIndex + offset >= content.length)
        {
            return null;
        }

        return content[tokenIndex + offset];
    }

    private Character _consume()
    {
        columnIndex++;
        return content[tokenIndex++];
    }
}

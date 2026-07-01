package Lexer;

import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedComments;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.LexingException;
import Lexer.Readers.NumericReader;
import Lexer.Readers.OperatorReader;
import Lexer.Readers.StringLiteralReader;
import Lexer.Readers.WordReader;
import Lexer.Tokens.PonctuationToken;
import Lexer.Tokens.Token;
import Entities.Common.Location.FileLocation;
import Entities.Common.Location.LocationPoint;

import java.util.ArrayList;

public class Lexer
{
    private boolean tokenized;
    private final String file;
    private final LexerCursor cursor;
    private final TokenBuffer buffer;
    private final WordReader wordReader;
    private final NumericReader numericReader;
    private final OperatorReader operatorReader;
    private final StringLiteralReader stringReader;

    private Lexer(char[] content, String file)
    {
        this.file = file;
        this.tokenized = false;
        this.buffer = TokenBuffer.create();
        this.cursor = LexerCursor.create(content);
        this.wordReader = WordReader.create(cursor, buffer, file);
        this.numericReader = NumericReader.create(cursor, buffer, file);
        this.operatorReader = OperatorReader.create(cursor, buffer, file);
        this.stringReader = StringLiteralReader.create(cursor, buffer, file);
    }

    public static Lexer create(char[] content, String file)
    {
        return new Lexer(content, file);
    }

    public ArrayList<Token> tokenize()
    {
        if (tokenized) throw new LexingException("Conteúdo já foi transformado em símbolo.", ErrorType.AlreadyTokenized);
        tokenized = true;

        while (cursor.peek() != null)
        {
            char current = cursor.peek();

            if (ReservedComments.isInlineComment(current))
            {
                consumeInlineComment();
            }
            else if (ReservedComments.isOpenMultiLineComment(current, cursor.peek(1)))
            {
                consumeMultiLineComment();
            }
            else if (PonctuationToken.isOpenParenthesis(current))
            {
                consumeAndAdd(TokenType.OPEN_PARENTHESIS, current);
            }
            else if (PonctuationToken.isCloseParenthesis(current))
            {
                consumeAndAdd(TokenType.CLOSE_PARENTHESIS, current);
            }
            else if (PonctuationToken.isInterrogation(current))
            {
                consumeAndAdd(TokenType.INTERROGATION, current);
            }
            else if (PonctuationToken.isOpenBrace(current))
            {
                consumeAndAdd(TokenType.OPEN_BRACE, current);
            }
            else if (PonctuationToken.isCloseBrace(current))
            {
                consumeAndAdd(TokenType.CLOSE_BRACE, current);
            }
            else if (PonctuationToken.isOpenBrackets(current))
            {
                consumeAndAdd(TokenType.OPEN_BRACKETS, current);
            }
            else if (PonctuationToken.isCloseBrackets(current))
            {
                consumeAndAdd(TokenType.CLOSE_BRACKETS, current);
            }
            else if (PonctuationToken.isDot(current))
            {
                consumeAndAdd(TokenType.DOT, current);
            }
            else if (PonctuationToken.isQuotationMark(current))
            {
                stringReader.read();
            }
            else if (Token.isAlphabeticOperator(current) || ReservedOperators.isReserved(Character.toString(current)))
            {
                operatorReader.read(cursor.consume());
            }
            else if (Token.isEquals(current))
            {
                consumeAndAdd(TokenType.EQUALS, current);
            }
            else if (PonctuationToken.isSemicolon(current))
            {
                consumeAndAdd(TokenType.SEMICOLON, current);
            }
            else if (PonctuationToken.isColon(current))
            {
                consumeAndAdd(TokenType.COLON, current);
            }
            else if (PonctuationToken.isComma(current))
            {
                consumeAndAdd(TokenType.COMMA, current);
            }
            else if (Token.isAlphabetic(current))
            {
                wordReader.read(cursor.consume());
            }
            else if (Token.isNumeric(current))
            {
                numericReader.read(cursor.consume());
            }
            else if (Token.isNewLine(current))
            {
                cursor.consume();
                cursor.advanceLine();
            }
            else if (Token.isIgnorable(current))
            {
                cursor.consume();
            }
            else
            {
                FileLocation loc = cursor.currentLocation();
                char symbol = cursor.consume();
                LocationPoint point = LocationPoint.create(loc, loc, file);
                throw new LexingException(
                    String.format("Símbolo inesperado %s.", symbol),
                    ErrorType.UnexpectedSymbol,
                    point
                );
            }
        }

        addEof();
        return buffer.toList();
    }

    private void consumeInlineComment()
    {
        while (cursor.peek() != null && cursor.peek() != '\n')
            cursor.consume();
    }

    private void consumeMultiLineComment()
    {
        while (cursor.peek() != null && !ReservedComments.isCloseMultiLineComment(cursor.peek(), cursor.peek(1)))
        {
            if (Token.isNewLine(cursor.consume())) cursor.advanceLine();
        }

        if (cursor.peek() != null) cursor.consume();
        if (cursor.peek() != null) cursor.consume();
    }

    private void consumeAndAdd(TokenType type, char value)
    {
        FileLocation start = cursor.currentLocation();
        cursor.consume();
        FileLocation end = cursor.currentLocation();
        buffer.emit(type, Character.toString(value), start, end, file);
    }

    private void addEof()
    {
        FileLocation location = cursor.currentLocation();
        LocationPoint locationPoint = LocationPoint.create(location, location, file);
        buffer.emitReserved(Token.create(TokenType.EOF, "", locationPoint));
    }
}
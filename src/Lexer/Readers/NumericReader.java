package Lexer.Readers;

import Entities.Enums.Lexer.TokenType;
import Lexer.LexerCursor;
import Lexer.TokenBuffer;
import Lexer.Tokens.PonctuationToken;
import Entities.Common.Location.FileLocation;

public class NumericReader
{
    private final LexerCursor cursor;
    private final TokenBuffer buffer;
    private final String file;

    private NumericReader(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        this.cursor = cursor;
        this.buffer = buffer;
        this.file = file;
    }

    public static NumericReader create(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        return new NumericReader(cursor, buffer, file);
    }

    public void read(char first)
    {
        FileLocation start = cursor.currentLocation();
        StringBuilder value = new StringBuilder(Character.toString(first));
        TokenType type = TokenType.INTEGER_LITERAL;

        while (cursor.peek() != null && (Character.isDigit(cursor.peek()) || PonctuationToken.isDot(cursor.peek())))
        {
            if (PonctuationToken.isDot(cursor.peek()))
            {
                if (type == TokenType.FLOAT_LITERAL) break;
                type = TokenType.FLOAT_LITERAL;
            }
            value.append(cursor.consume());
        }

        FileLocation end = cursor.currentLocation();
        buffer.emit(type, value.toString(), start, end, file);
    }
}
package Lexer.Readers;

import Entities.Enums.Lexer.TokenType;
import Lexer.LexerCursor;
import Lexer.TokenBuffer;
import Lexer.Tokens.PonctuationToken;
import Lexer.Tokens.Token;
import Entities.Common.Location.FileLocation;

public class StringLiteralReader
{
    private final LexerCursor cursor;
    private final TokenBuffer buffer;
    private final String file;

    private StringLiteralReader(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        this.cursor = cursor;
        this.buffer = buffer;
        this.file = file;
    }

    public static StringLiteralReader create(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        return new StringLiteralReader(cursor, buffer, file);
    }

    public void read()
    {
        FileLocation start = cursor.currentLocation();
        cursor.consume();
        StringBuilder value = new StringBuilder();

        while (cursor.peek() != null && !PonctuationToken.isQuotationMark(cursor.peek()))
        {
            if (PonctuationToken.isBackslash(cursor.peek()))
            {
                String escape = readEscape();
                value.append(escape);
                if (Token.isNewLine(escape))
                {
                    cursor.advanceLine();
                }
                continue;
            }

            value.append(cursor.consume());
        }

        if (cursor.peek() != null) cursor.consume();
        FileLocation end = cursor.currentLocation();
        buffer.emit(TokenType.STRING_LITERAL, value.toString(), start, end, file);
    }

    private String readEscape()
    {
        cursor.consume();
        if (cursor.peek() == null) return "\\";

        char escaped = cursor.consume();
        return switch (escaped)
        {
            case 'n'  -> "\n";
            case 't'  -> "\t";
            case 'r'  -> "\r";
            case 'b'  -> "\b";
            case 'f'  -> "\f";
            case '\\' -> "\\";
            case '\'' -> "'";
            case '"'  -> "\"";
            default   -> "\\" + escaped;
        };
    }
}
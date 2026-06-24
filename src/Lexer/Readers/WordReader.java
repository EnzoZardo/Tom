package Lexer.Readers;

import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedWords;
import Entities.Enums.Lexer.TokenType;
import Lexer.LexerCursor;
import Lexer.TokenBuffer;
import Entities.Common.Location.FileLocation;

public class WordReader
{
    private final LexerCursor cursor;
    private final TokenBuffer buffer;
    private final String file;

    private WordReader(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        this.cursor = cursor;
        this.buffer = buffer;
        this.file = file;
    }

    public static WordReader create(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        return new WordReader(cursor, buffer, file);
    }

    public void read(char first)
    {
        StringBuilder value = new StringBuilder(Character.toString(first));
        FileLocation start = cursor.currentLocation();

        while (cursor.peek() != null && (Character.isAlphabetic(cursor.peek()) || ReservedKeys.Underline == cursor.peek()))
        {
            value.append(cursor.consume());
        }

        String text = value.toString();
        FileLocation end = cursor.currentLocation();

        if (ReservedWords.isReserved(text))
        {
            buffer.emitReserved(ReservedWords.token(text, start, end, file));
            return;
        }

        buffer.emit(TokenType.IDENTIFIER, text, start, end, file);
    }
}
package Lexer.Readers;

import Entities.Constants.ReservedOperators;
import Lexer.LexerCursor;
import Lexer.TokenBuffer;
import Entities.Common.Location.FileLocation;

public class OperatorReader
{
    private final LexerCursor cursor;
    private final TokenBuffer buffer;
    private final WordReader wordReader;
    private final String file;

    private OperatorReader(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        this.file = file;
        this.cursor = cursor;
        this.buffer = buffer;
        this.wordReader = WordReader.create(cursor, buffer, file);
    }

    public static OperatorReader create(LexerCursor cursor, TokenBuffer buffer, String file)
    {
        return new OperatorReader(cursor, buffer, file);
    }

    public void read(char first)
    {
        if (Character.isAlphabetic(first))
        {
            wordReader.read(first);
            return;
        }

        StringBuilder value = new StringBuilder(Character.toString(first));
        FileLocation start = cursor.currentLocation();

        while (cursor.peek() != null && ReservedOperators.isReserved(value.toString() + cursor.peek()))
        {
            value.append(cursor.consume());
        }

        FileLocation end = cursor.currentLocation();
        buffer.emitReserved(ReservedOperators.token(value.toString(), start, end, file));
    }
}
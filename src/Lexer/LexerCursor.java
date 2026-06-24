package Lexer;

import Entities.Common.Location.FileLocation;

public class LexerCursor
{
    private int columnIndex;
    private int lineIndex;
    private int tokenIndex;
    private final char[] content;

    private LexerCursor(char[] content)
    {
        this.content = content;
        this.columnIndex = 0;
        this.lineIndex = 1;
        this.tokenIndex = 0;
    }

    public static LexerCursor create(char[] content)
    {
        return new LexerCursor(content);
    }

    public Character peek()
    {
        return peek(0);
    }

    public Character peek(int offset)
    {
        int index = tokenIndex + offset;
        return index < content.length ? content[index] : null;
    }

    public char consume()
    {
        columnIndex++;
        return content[tokenIndex++];
    }

    public void advanceLine()
    {
        columnIndex = 0;
        lineIndex++;
    }

    public FileLocation currentLocation()
    {
        return FileLocation.create(columnIndex, lineIndex, tokenIndex);
    }

    public boolean isAtEnd()
    {
        return tokenIndex >= content.length;
    }
}
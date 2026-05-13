package Lexer.Tokens;

public class TokenFileLocation
{
    public int line;
    public int column;
    public int index;

    private TokenFileLocation(int column, int line, int index)
    {
        this.line = line;
        this.column = column;
        this.index = index;
    }

    public static TokenFileLocation create(int column, int line, int index)
    {
        return new TokenFileLocation(column, line, index);
    }

    @Override
    public String toString()
    {
        return "{ line: " + line + ", column: " + column + ", index: " + index + " }";
    }
}

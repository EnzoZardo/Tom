package Lexer.Tokens;

public class TokenLocation
{
    public TokenFileLocation start;
    public TokenFileLocation end;

    private TokenLocation(
        TokenFileLocation start,
        TokenFileLocation end
    )
    {
        this.start = start;
        this.end = end;
    }

    public static TokenLocation create(
        TokenFileLocation start,
        TokenFileLocation end
    )
    {
        return new TokenLocation(start, end);
    }

    @Override
    public String toString()
    {
        return "{ start: " + start + ", end: " + end + " }";
    }
}

package Entities.Common.Location;

public class FileLocation
{
    public final int column;
    public final int index;
    public final int line;

    private FileLocation(int column, int line, int index)
    {
        this.line = line;
        this.column = column;
        this.index = index;
    }

    public static FileLocation create(int column, int line, int index)
    {
        return new FileLocation(column, line, index);
    }

    @Override
    public String toString()
    {
        return "{ line: " + line
            + ", column: " + column
            + ", index: " + index
            + " }";
    }
}

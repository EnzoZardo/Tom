package Entities.Common.Location;

public class LocationPoint
{
    public final String file;
    public final FileLocation end;
    public final FileLocation start;

    private LocationPoint(FileLocation start, FileLocation end, String file)
    {
        this.end = end;
        this.file = file;
        this.start = start;
    }

    public static LocationPoint create(FileLocation start, FileLocation end, String file)
    {
        return new LocationPoint(start, end, file);
    }

    @Override
    public String toString()
    {
        return "{ start: " + start
            + ", end: " + end
            + ", file: " + file
            + " }";
    }
}

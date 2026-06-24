package Entities.Common.Result;

import Entities.Common.Location.LocationPoint;

public class Error
{
    private final LocationPoint location;
    private final String message;
    private ErrorType type;

    private Error(String message)
    {
        this.location = null;
        this.message = message;
        this.type = ErrorType.Any;
    }

    private Error(String message, ErrorType type)
    {
        this.type = type;
        this.location = null;
        this.message = message;
    }

    private Error(String message, ErrorType type, LocationPoint location)
    {
        this.type = type;
        this.message = message;
        this.location = location;
    }

    public static Error create(String message)
    {
        return new Error(message);
    }
    public static Error create(String message, ErrorType type)
    {
        return new Error(message, type);
    }
    public static Error create(String message, ErrorType type, LocationPoint location)
    {
        return new Error(message, type, location);
    }

    public String getMessage()
    {
        return message;
    }

    public ErrorType getType()
    {
        return type;
    }

    public int getExit()
    {
        return type.ordinal();
    }

    public String getLocation()
    {
        if (location == null) return "";

        return String.format("O erro está em:\nCAMINHO '%s'\nEM coluna %d da linha %d\nATÉ coluna %d da linha %d",
            location.file,
            location.start.column,
            location.start.line,
            location.end.column,
            location.end.line);
    }

    public void setType(ErrorType type)
    {
        this.type = type;
    }
}

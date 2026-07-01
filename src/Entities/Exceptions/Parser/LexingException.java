package Entities.Exceptions.Parser;

import Entities.Common.Location.LocationPoint;
import Entities.Common.Result.ErrorType;

public class LexingException extends RuntimeException
{
    private final ErrorType errorType;
    private final LocationPoint location;

    public LexingException(String message)
    {
        super(message);
        this.errorType = ErrorType.UnexpectedSymbol;
        this.location = null;
    }

    public LexingException(String message, ErrorType errorType)
    {
        super(message);
        this.errorType = errorType;
        this.location = null;
    }

    public LexingException(String message, ErrorType errorType, LocationPoint location)
    {
        super(message);
        this.errorType = errorType;
        this.location = location;
    }

    public ErrorType getErrorType()
    {
        return errorType;
    }

    public LocationPoint getLocation()
    {
        return location;
    }

    public int getExit()
    {
        return errorType.ordinal();
    }
}

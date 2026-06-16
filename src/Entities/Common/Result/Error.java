package Entities.Common.Result;

public class Error
{
    private final String message;
    private ErrorType type;

    private Error(String message)
    {
        this.message = message;
        this.type = ErrorType.Any;
    }

    private Error(String message, ErrorType type)
    {
        this.message = message;
        this.type = type;
    }

    public static Error create(String message)
    {
        return new Error(message);
    }
    public static Error create(String message, ErrorType type)
    {
        return new Error(message, type);
    }

    public String getMessage()
    {
        return message;
    }

    public ErrorType getType()
    {
        return type;
    }

    public void setType(ErrorType type)
    {
        this.type = type;
    }

    public <T> ErrorOr<T> errorOr()
    {
        return ErrorOr.Fail(this);
    }
}

package Entities.Common.Result;

public class ErrorOr<T>
{
    public T value = null;
    public Error error = null;
    protected boolean success;

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    public ErrorOr(T value)
    {
        this.success = true;
        this.value = value;
    }

    public ErrorOr(Error error)
    {
        this.success = false;
        this.error = error;
    }

    public static <T> ErrorOr<T> Ok(T value)
    {
        return new ErrorOr<>(value);
    }
    public static ErrorOr<Void> Ok()
    {
        return new ErrorOr<>(null);
    }
    public static <T> ErrorOr<T> Fail(Error error)
    {
        return new ErrorOr<>(error);
    }
    public static <T> ErrorOr<T> Fail(String message)
    {
        return new ErrorOr<>(Error.create(message));
    }

    public ErrorOr<Void> empty()
    {
        if (isError())
        {
            return ErrorOr.Fail(error);
        }
        return ErrorOr.Ok(null);
    }
}
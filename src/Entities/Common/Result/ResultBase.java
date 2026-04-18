package Entities.Common.Result;

public class ResultBase
{
    protected boolean success;
    protected Error error = null;

    protected ResultBase(Error error)
    {
        this.success = false;
        this.error = error;
    }

    protected ResultBase()
    {
        this.success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}

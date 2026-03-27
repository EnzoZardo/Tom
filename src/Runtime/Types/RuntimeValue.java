package Runtime.Types;

import Runtime.Types.Enums.ValueType;

public abstract class RuntimeValue
{
    public final ValueType type;

    protected RuntimeValue(ValueType type)
    {
        this.type = type;
    }

    public abstract String print(int level);

    @Override
    public String toString() {
        return print(0);
    }
}

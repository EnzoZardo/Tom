package Entities.Abstractions.Runtime;

import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.NotIterableException;

public abstract class RuntimeValue
{
    public final ValueType type;

    protected RuntimeValue(ValueType type)
    {
        this.type = type;
    }

    public abstract String print(int level);
    public abstract boolean equals(RuntimeValue that);
    public abstract boolean bool();

    public boolean not()
    {
        return !bool();
    }

    public RuntimeValue iterate(int index)
    {
        throw new NotIterableException("Tipo de valor não iterável.");
    }

    public int iteratorSize()
    {
        throw new NotIterableException("Tipo de valor não iterável.");
    }

    @Override
    public String toString() {
        return print(0);
    }
}

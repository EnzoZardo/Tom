package Entities.Abstractions.Runtime;

import Entities.Enums.Runtime.ValueType;

public abstract class RuntimeException extends RuntimeValue
{
    protected RuntimeException(ValueType type)
    {
        super(type);
    }
}

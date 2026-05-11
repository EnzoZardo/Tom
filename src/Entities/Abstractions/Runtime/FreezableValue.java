package Entities.Abstractions.Runtime;

import Entities.Enums.Runtime.ValueType;

public abstract class FreezableValue extends RuntimeValue
{
    protected boolean frozen;

    protected FreezableValue(ValueType type, boolean frozen)
    {
        super(type);
        this.freezable = true;
        this.frozen = frozen;
    }

    public boolean isFrozen()
    {
        return frozen;
    }

    public RuntimeValue freezeMe() {
        this.frozen = true;
        return this;
    }
}

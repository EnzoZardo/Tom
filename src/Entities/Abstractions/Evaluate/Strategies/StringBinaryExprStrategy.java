package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;

public interface StringBinaryExprStrategy
{
    RuntimeValue evaluate(RuntimeValue right, RuntimeValue left);
}

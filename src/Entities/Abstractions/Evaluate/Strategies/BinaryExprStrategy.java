package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;

public interface BinaryExprStrategy
{
    RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator);
}

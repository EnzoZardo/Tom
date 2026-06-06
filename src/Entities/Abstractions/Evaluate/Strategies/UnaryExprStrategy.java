package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;

public interface UnaryExprStrategy
{
    RuntimeValue evaluate(RuntimeValue right, String operator);
}

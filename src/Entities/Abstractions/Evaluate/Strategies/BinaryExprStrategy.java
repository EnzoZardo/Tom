package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;

public interface BinaryExprStrategy
{
    RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator);
}

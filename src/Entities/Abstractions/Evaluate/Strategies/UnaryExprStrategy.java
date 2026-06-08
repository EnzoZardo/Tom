package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;
import jdk.jshell.spi.ExecutionControl;

public interface UnaryExprStrategy
{
    RuntimeValue evaluate(RuntimeValue right, String operator);
}

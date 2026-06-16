package Entities.Abstractions.Evaluate.Strategies;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;

public interface CallExprStrategy
{
    RuntimeValue evaluate(CallExpr expr, RuntimeValue caller, Environment environment) throws AlreadyDeclaredVariableException;
}

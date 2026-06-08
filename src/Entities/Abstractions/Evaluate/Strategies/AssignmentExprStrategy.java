package Entities.Abstractions.Evaluate.Strategies;

import Ast.Expressions.AssignmentExpr;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;

public interface AssignmentExprStrategy
{
    RuntimeValue evaluate(AssignmentExpr assignment, Environment environment) throws AlreadyDeclaredVariableException;
}

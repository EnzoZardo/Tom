package Entities.Abstractions.Evaluate.Strategies;

import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;

public interface MemberAssignmentExprStrategy
{
    RuntimeValue evaluate(MemberExpr member, RuntimeValue value, Environment env) throws AlreadyDeclaredVariableException;
}

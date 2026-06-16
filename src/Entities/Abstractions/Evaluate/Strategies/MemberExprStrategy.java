package Entities.Abstractions.Evaluate.Strategies;

import Entities.Abstractions.Runtime.RuntimeValue;
import Ast.Expressions.MemberExpr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;

public interface MemberExprStrategy
{
    RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
        throws AlreadyDeclaredVariableException;
}

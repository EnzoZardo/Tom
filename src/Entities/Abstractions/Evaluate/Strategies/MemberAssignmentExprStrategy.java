package Entities.Abstractions.Evaluate.Strategies;

import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Environment;

public interface MemberAssignmentExprStrategy
{
    RuntimeValue evaluate(MemberExpr member, RuntimeValue value, Environment env);
}

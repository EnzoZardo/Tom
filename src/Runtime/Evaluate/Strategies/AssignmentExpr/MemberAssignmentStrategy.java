package Runtime.Evaluate.Strategies.AssignmentExpr;

import Ast.Expressions.AssignmentExpr;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.AssignmentExprStrategy;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.InvalidAssignmentExpression;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Evaluate.Factory.AssignmentExpr.Member.MemberAssignmentExprFactory;

public class MemberAssignmentStrategy implements AssignmentExprStrategy
{
    @Override
    public RuntimeValue evaluate(AssignmentExpr assignment, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ErrorOr<MemberAssignmentExprStrategy> result = MemberAssignmentExprFactory.build(assignment, environment);

        if (result.isError())
            throw new InvalidAssignmentExpression(result.error.getMessage());

        MemberAssignmentExprStrategy strategy = result.value;
        MemberExpr assigned = (MemberExpr) assignment.assigned;
        RuntimeValue value = Interpreter.evaluate(assignment.value, environment);

        return strategy.evaluate(assigned, value, environment);
    }
}

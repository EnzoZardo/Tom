package Runtime.Evaluate.Factory.AssignmentExpr.Member;

import Ast.Expressions.AssignmentExpr;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.AssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;
import Runtime.Evaluate.Strategies.AssignmentExpr.Member.AnonymousMemberAssignmentStrategy;
import Runtime.Interpreter;

public abstract class MemberAssignmentFactory {
    public static ErrorOr<AssignmentExprStrategy> build(AssignmentExpr assignment, Environment env)
        throws AlreadyDeclaredVariableException {
        MemberExpr member = (MemberExpr) assignment.assigned;
        RuntimeValue value = Interpreter.evaluate(assignment.value, env);

        if (AnonymousMemberAssignmentStrategy.canEvaluate(member))
        {
            return new AnonymousMemberAssignmentStrategy();
        }

        return null;
    }
}

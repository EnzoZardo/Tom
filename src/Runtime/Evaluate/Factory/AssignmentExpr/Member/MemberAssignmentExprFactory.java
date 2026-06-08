package Runtime.Evaluate.Factory.AssignmentExpr.Member;

import Ast.Expressions.AssignmentExpr;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Evaluate.Strategies.AssignmentExpr.Member.AnonymousMemberAssignmentStrategy;
import Runtime.Evaluate.Strategies.AssignmentExpr.Member.ClassIdentifierMemberAssignmentStrategy;
import Runtime.Evaluate.Strategies.AssignmentExpr.Member.ObjectIdentifierMemberAssignmentStrategy;

public abstract class MemberAssignmentExprFactory
{
    public static ErrorOr<MemberAssignmentExprStrategy> build (AssignmentExpr assignment, Environment env)
        throws AlreadyDeclaredVariableException {
        MemberExpr member = (MemberExpr) assignment.assigned;

        if (AnonymousMemberAssignmentStrategy.canEvaluate(member)) {
            return ErrorOr.Success(new AnonymousMemberAssignmentStrategy());
        }

        if (ClassIdentifierMemberAssignmentStrategy.canEvaluate(member, env)) {
            return ErrorOr.Success(new ClassIdentifierMemberAssignmentStrategy());
        }

        if (ObjectIdentifierMemberAssignmentStrategy.canEvaluate(member, env)) {
            return ErrorOr.Success(new ClassIdentifierMemberAssignmentStrategy());
        }

        RuntimeValue property = Interpreter.evaluate(member.property, env);

        return ErrorOr.Fail("Não é possível realizar a atribuição dos valores informados.");
    }
}

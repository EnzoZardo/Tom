package Runtime.Evaluate.Factory.AssignmentExpr.Member;

import Ast.Expressions.AssignmentExpr;
import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;
import Runtime.Evaluate.Strategies.AssignmentExpr.Member.*;
import Runtime.Interpreter;

public abstract class MemberAssignmentExprFactory
{
    public static ErrorOr<MemberAssignmentExprStrategy> build (AssignmentExpr assignment, Environment env)
        throws AlreadyDeclaredVariableException {
        MemberExpr member = (MemberExpr) assignment.assigned;

        if (AnonymousMemberAssignmentStrategy.canEvaluate(member))
        {
            return ErrorOr.Success(new AnonymousMemberAssignmentStrategy());
        }

        Identifier objectIdentifier = (Identifier) member.object;
        RuntimeValue variable = env.lookupVariable(objectIdentifier.value);

        if (ClassIdentifierMemberAssignmentStrategy.canEvaluate(member, variable))
        {
            return ErrorOr.Success(new ClassIdentifierMemberAssignmentStrategy());
        }

        if (ObjectIdentifierMemberAssignmentStrategy.canEvaluate(member, variable))
        {
            return ErrorOr.Success(new ObjectIdentifierMemberAssignmentStrategy());
        }

        RuntimeValue property = Interpreter.evaluate(member.property, env);

        if (ObjectComputedMemberAssigmentStrategy.canEvaluate(member, variable, property))
        {
            return ErrorOr.Success(new ObjectComputedMemberAssigmentStrategy());
        }

        if (ArrayComputedMemberAssignmentStrategy.canEvaluate(member, variable, property))
        {
            return ErrorOr.Success(new ArrayComputedMemberAssignmentStrategy());
        }

        return ErrorOr.Fail("Não é possível realizar a atribuição dos valores informados.");
    }
}

package Runtime.Evaluate.Strategies.AssignmentExpr.Member;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Runtime.Environment;

public class ObjectIdentifierMemberAssignmentStrategy implements MemberAssignmentExprStrategy
{
    public static boolean canEvaluate(MemberExpr member, RuntimeValue variable)
    {
        return !member.computed
            && variable.type == ValueType.Object
            && member.object.type == NodeType.Identifier;
    }

    @Override
    public RuntimeValue evaluate(MemberExpr member, RuntimeValue value, Environment environment)
    {
        Identifier memberIdentifier = (Identifier) member.property;
        Identifier objectIdentifier = (Identifier) member.object;
        return environment.assignMember(objectIdentifier.value, memberIdentifier.value, value);
    }
}

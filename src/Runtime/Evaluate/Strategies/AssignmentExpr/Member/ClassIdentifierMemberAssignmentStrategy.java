package Runtime.Evaluate.Strategies.AssignmentExpr.Member;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Runtime.Environment;

public class ClassIdentifierMemberAssignmentStrategy implements MemberAssignmentExprStrategy
{
    public static boolean canEvaluate(MemberExpr member, Environment environment) {
        Identifier objectIdentifier = (Identifier) member.object;
        RuntimeValue variable = environment.lookupVariable(objectIdentifier.value);
        return member.object.type == NodeType.Identifier && !member.computed && variable.type == ValueType.Class;
    }

    @Override
    public RuntimeValue evaluate(MemberExpr member, RuntimeValue value, Environment environment) {
        Identifier memberIdentifier = (Identifier) member.property;
        Identifier objectIdentifier = (Identifier) member.object;
        return environment.assignClassMember(objectIdentifier.value, memberIdentifier.value, value);
    }
}

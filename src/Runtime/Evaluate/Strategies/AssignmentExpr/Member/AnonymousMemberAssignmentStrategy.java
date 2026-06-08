package Runtime.Evaluate.Strategies.AssignmentExpr.Member;

import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Runtime.Environment;

public class AnonymousMemberAssignmentStrategy implements MemberAssignmentExprStrategy {
    public static boolean canEvaluate(MemberExpr member) {
        return member.object.type == NodeType.ObjectLiteral || member.object.type == NodeType.ArrayLiteral;
    }

    @Override
    public RuntimeValue evaluate(MemberExpr __, RuntimeValue value, Environment ___) {
        return value;
    }
}

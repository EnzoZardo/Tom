package Runtime.Evaluate.Factory.AssignmentExpr;

import Ast.Expressions.AssignmentExpr;
import Entities.Abstractions.Evaluate.Strategies.AssignmentExprStrategy;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Runtime.Evaluate.Strategies.AssignmentExpr.IdentifierAssignmentStrategy;
import Runtime.Evaluate.Strategies.AssignmentExpr.MemberAssignmentStrategy;

public abstract class AssignmentExprFactory
{
    public static ErrorOr<AssignmentExprStrategy> build(AssignmentExpr assignment)
    {
        return switch (assignment.assigned.type)
        {
            case NodeType.MemberExpression -> ErrorOr.Success(new MemberAssignmentStrategy());
            case NodeType.Identifier -> ErrorOr.Success(new IdentifierAssignmentStrategy());
            default -> ErrorOr.Fail("Esperávamos um membro de objeto ou " +
                "uma variável para a expressão darmos um novo valor para ela.");
        };
    }
}

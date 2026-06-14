package Runtime.Evaluate.Strategies.MemberExpr;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.Evaluate.InvalidComputedClassMemberExpr;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.ClassValue;
import Runtime.Values.NullValue;
import Runtime.Environment;

public class ClassMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment __)
    {
        InvalidComputedClassMemberExpr.throwIfComputed(expr);
        ClassValue value = (ClassValue) owner;

        if (expr.property.type == NodeType.Identifier) {
            Identifier identifier = (Identifier) expr.property;
            ClassMemberValue member = value.members.get(identifier.value);
            ClassMemberValue bound = (ClassMemberValue) member.copy();

            bound.owner = value;
            return bound;
        }

        // Todo: change this
        return NullValue.create();
    }
}

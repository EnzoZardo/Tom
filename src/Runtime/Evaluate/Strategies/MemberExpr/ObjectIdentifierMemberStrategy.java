package Runtime.Evaluate.Strategies.MemberExpr;

import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Abstractions.Runtime.RuntimeValue;
import Ast.Expressions.MemberExpr;
import Ast.Expressions.Identifier;
import Runtime.Values.NullValue;
import Runtime.Values.ObjectValue;
import Runtime.Environment;

public class ObjectIdentifierMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
            throws AlreadyDeclaredVariableException
    {
        ObjectValue value = (ObjectValue) owner;

        Identifier id = (Identifier) expr.property;

        if (value.properties.containsKey(id.value)) {
            return value.properties.get(id.value);
        }

        return NullValue.create();
    }
}
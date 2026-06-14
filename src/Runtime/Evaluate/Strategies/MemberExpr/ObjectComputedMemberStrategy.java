package Runtime.Evaluate.Strategies.MemberExpr;

import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.InvalidComputedObjectKeyType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Ast.Expressions.MemberExpr;
import Runtime.Values.NumericValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;
import Runtime.Interpreter;
import Runtime.Environment;

public class ObjectComputedMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        RuntimeValue member = Interpreter.evaluate(expr.property, environment);
        ObjectValue value = (ObjectValue) owner;

        if (member.type == ValueType.String)
        {
            StringValue id = (StringValue) member;

            if (value.properties.containsKey(id.value))
            {
                return value.properties.get(id.value);
            }
        }

        if (member.type == ValueType.Numeric)
        {
            NumericValue id = (NumericValue) member;

            if (id.isInteger && value.properties.size() > (int) id.value)
            {
                return (RuntimeValue) value.properties.values().toArray()[(int) id.value];
            }
        }

        throw new InvalidComputedObjectKeyType();
    }
}

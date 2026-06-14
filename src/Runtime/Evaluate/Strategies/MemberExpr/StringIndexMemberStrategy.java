package Runtime.Evaluate.Strategies.MemberExpr;

import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Exceptions.Evaluate.InvalidArrayIndexTypeException;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Runtime.Values.NumericValue;
import Ast.Expressions.MemberExpr;
import Runtime.Values.StringValue;
import Runtime.Values.NullValue;
import Runtime.Interpreter;
import Runtime.Environment;

public class StringIndexMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
            throws AlreadyDeclaredVariableException
    {
        StringValue value = (StringValue) owner;

        RuntimeValue member = Interpreter.evaluate(expr.property, environment);

        if (member.type == ValueType.Numeric)
        {
            NumericValue key = (NumericValue) member;

            if (!key.isInteger || key.value < 0)
            {
                throw new InvalidArrayIndexTypeException();
            }

            int index = (int)key.value;
            if (index < value.value.length())
            {
                return StringValue.create(Character.toString(value.value.charAt(index)));
            }

            return NullValue.create();
        }

        throw new InvalidArrayIndexTypeException();
    }
}


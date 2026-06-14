package Runtime.Evaluate.Strategies.MemberExpr;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.InvalidArrayIndexTypeException;
import Runtime.Values.*;
import Runtime.Environment;
import Runtime.Interpreter;

public class ArrayIndexMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        ArrayValue value = (ArrayValue) owner;

        RuntimeValue member = Interpreter.evaluate(expr.property, environment);

        if (member.type == ValueType.Numeric)
        {
            NumericValue key = (NumericValue) member;

            if (!key.isInteger || key.value < 0)
            {
                throw new InvalidArrayIndexTypeException();
            }

            int index = (int) key.value;
            if (value.items.containsKey(index))
            {
                return value.items.get(index);
            }

            return NullValue.create();
        }

        throw new InvalidArrayIndexTypeException();
    }
}


package Runtime.Evaluate.Factory.CallExpr;

import Entities.Abstractions.Evaluate.Strategies.CallExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.Errors;
import Entities.Enums.Runtime.ValueType;
import Runtime.Evaluate.Strategies.CallExpr.ClassMemberCallStrategy;
import Runtime.Evaluate.Strategies.CallExpr.ConstructorCallStrategy;
import Runtime.Evaluate.Strategies.CallExpr.FunctionCallStrategy;
import Runtime.Evaluate.Strategies.CallExpr.NativeFunctionCallStrategy;
import Runtime.Values.ClassMemberValue;

public abstract class CallExprFactory
{
    public static ErrorOr<CallExprStrategy> build(RuntimeValue caller)
    {
        if (caller.type == ValueType.Function)
            return ErrorOr.Success(new FunctionCallStrategy());

        if (caller.type == ValueType.ClassMember)
        {
            ClassMemberValue member = (ClassMemberValue) caller;

            if (member.value.type == ValueType.Function)
                return ErrorOr.Success(new ClassMemberCallStrategy());
        }

        if (caller.type == ValueType.NativeFunction)
            return ErrorOr.Success(new NativeFunctionCallStrategy());

        if (caller.type == ValueType.Class)
            return ErrorOr.Success(new ConstructorCallStrategy());

        return Errors.invalidCall("Valor informado não permite ser chamado como uma função.");
    }
}

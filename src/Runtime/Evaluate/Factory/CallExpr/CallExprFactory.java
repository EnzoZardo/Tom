package Runtime.Evaluate.Factory.CallExpr;

import Entities.Abstractions.Evaluate.Strategies.CallExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.Errors;
import Entities.Enums.Runtime.ValueType;

public class CallExprFactory
{
    public static ErrorOr<CallExprStrategy> build(RuntimeValue caller)
    {
        if (caller.type == ValueType.NativeFunction)
        {

        }

        return Errors.invalidCall("Valor informado não permite ser chamado como uma função.");
    }
}

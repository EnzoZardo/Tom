package Runtime.Evaluate.Strategies;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.Evaluate.InvalidOperatorException;
import Runtime.Evaluate.Factory.BInaryExpr.InArrayStrategyFactory;
import Runtime.Evaluate.Strategies.Operators.In.Array.*;
import Runtime.Values.*;

public class BooleanBinaryStrategy implements BinaryExprStrategy
{
    public static boolean canEvaluate(String operator)
    {
        return ReservedOperators.isBooleanOperator(operator);
    }

    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator)
    {
        return switch (operator)
        {
            case ReservedKeys.In -> evaluateInOperator(left, right);
            case ReservedKeys.Or -> BooleanValue.create(left.bool() || right.bool());
            case ReservedKeys.And -> BooleanValue.create(left.bool() && right.bool());
            case ReservedKeys.Equality -> BooleanValue.create(left.equals(right));
            case ReservedKeys.Difference -> BooleanValue.create(!left.equals(right));
            case ReservedKeys.Minor,
                 ReservedKeys.Greater,
                 ReservedKeys.MinorOrEqual,
                 ReservedKeys.GreaterOrEqual -> evaluateSizeOperator(left, right, operator);
            default -> throw new InvalidOperatorException(operator);
        };
    }
    public static BooleanValue evaluateSizeOperator(RuntimeValue left, RuntimeValue right, String operator)
    {
        if (left.type != ValueType.Numeric || right.type != ValueType.Numeric)
        {
            throw new InvalidBinaryOperation(String.format("A operação %s só é permitida entre valores numéricos.",
                operator));
        }

        NumericValue rightValue = (NumericValue) right;
        NumericValue leftValue = (NumericValue) left;

        boolean result = switch (operator)
        {
            case ReservedKeys.Minor -> leftValue.value < rightValue.value;
            case ReservedKeys.Greater -> leftValue.value > rightValue.value;
            case ReservedKeys.MinorOrEqual -> leftValue.value <= rightValue.value;
            case ReservedKeys.GreaterOrEqual -> leftValue.value >= rightValue.value;
            default -> throw new InvalidOperatorException(operator);
        };

        return BooleanValue.create(result);
    }

    private static RuntimeValue evaluateInOperator(RuntimeValue left, RuntimeValue right) {
        if (right.type == ValueType.Array)
        {
            InArrayBase contained = InArrayStrategyFactory.build(left, (ArrayValue) right);
            return contained.evaluate(left);
        }

        if (right.type == ValueType.String)
        {
            StringValue stringValue = (StringValue) right;

            if (left.type != ValueType.String)
            {
                throw new InvalidBinaryOperation("Somente textos podem ser usados para testar se estão em textos.");
            }

            return BooleanValue.create(stringValue.value.contains(((StringValue)left).value));
        }

        if (right.type == ValueType.Object) {
            ObjectValue objectValue = (ObjectValue) right;
            boolean contained = switch (left.type) {
                case ValueType.String ->
                {
                    StringValue value = (StringValue)left;
                    yield objectValue.properties.keySet().stream().anyMatch(value.value::equals);
                }
                case ValueType.Array ->
                {
                    ArrayValue value = (ArrayValue)left;
                    if (value.items.size() > 2)
                    {
                        yield false;
                    }

                    for (int i = 0; i < objectValue.iteratorSize(); i++)
                    {
                        ArrayValue entry = (ArrayValue) objectValue.iterate(i);
                        if (value.equals(entry))
                        {
                            yield true;
                        }
                    }

                    yield false;
                }
                default -> throw new InvalidBinaryOperation("Valor não permitido para ser verificado se está em objeto.");
            };
            return BooleanValue.create(contained);
        }

        throw new InvalidBinaryOperation("Só é permitido verificar se um valor está presente em listas, objetos ou textos.");
    }
}
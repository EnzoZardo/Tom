package Ast.Types;

import Ast.Types.Primitive.NeverType;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Lexer.TokenType;
import Entities.Enums.TypeKind;
import Entities.Exceptions.Parser.ParsingException;
import Lexer.Tokens.Token;
import Parser.Parser;
import Runtime.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryType extends Type
{
    public final Type left;
    public final Type right;

    protected BinaryType(Type left, Type right)
    {
        super(TypeKind.BinaryType);
        this.left = left;
        this.right = right;
    }

    public static BinaryType create(Type left, Type right)
    {
        return new BinaryType(left, right);
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type != TypeKind.BinaryType)
        {
            return FunctionType.reduce(env, type);
        }

        BinaryType binaryType = (BinaryType) type;

        return BinaryType.create(Type.reduce(env, binaryType.left), Type.reduce(env, binaryType.right));
    }

    public static Type parse(Parser parser)
    {
        Type left = FunctionType.parse(parser);

        if (parser.peekIs(TokenType.BINARY_OPERATOR)
            && !ReservedKeys.Minor.equals(parser.peekValue())
            && !ReservedKeys.Greater.equals(parser.peekValue()))
        {
            Token token = parser.consume();
            if (ReservedKeys.Or.equals(token.value))
            {
                Type right = Type.parse(parser);
                return BinaryType.create(left, right);
            }

            if (ReservedKeys.And.equals(token.value))
            {
                Type right = Type.parse(parser);
                return handleIntersection(left, right);
            }

            throw new ParsingException("Tipos Binários somente aceitam os operadores \"ou\" e \"e\" mas recebemos "
                    + "outro operador no código - " + token.value, ErrorType.ParsingError, token.location);
        }

        return left;
    }

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.BinaryType)
        {
            return FunctionType.equals(type1, type2);
        }

        BinaryType binary1 = (BinaryType) type1;
        BinaryType binary2 = (BinaryType) type2;

        if (Type.equals(binary1.left, binary2.left).isSuccess())
            return ErrorOr.Success();

        if (Type.equals(binary1.right, binary2.right).isSuccess())
            return ErrorOr.Success();

        if (Type.equals(binary1.right, binary2.left).isSuccess())
            return ErrorOr.Success();

        if (Type.equals(binary1.left, binary2.right).isSuccess())
            return ErrorOr.Success();

        return ErrorOr.Fail("Os tipos binários são diferentes.");
    }

    private static Type handleIntersection(Type left, Type right)
    {
        if (left.type == TypeKind.ObjectType && right.type == TypeKind.ObjectType)
        {
            ObjectType obj1 = (ObjectType) left;
            ObjectType obj2 = (ObjectType) right;

            ArrayList<ObjectTypeProperty> props = Stream
                .of(obj1.properties, obj2.properties)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));

            return ObjectType.create(props);
        }

        if (left.type == TypeKind.ObjectType && right.type == TypeKind.BinaryType)
        {
            BinaryType binary = (BinaryType) right;
            Type leftIntersection = handleIntersection(left, binary.left);
            Type rightIntersection = handleIntersection(left, binary.right);
            return BinaryType.create(leftIntersection, rightIntersection);
        }

        if (left.type == TypeKind.BinaryType && right.type == TypeKind.ObjectType)
        {
            BinaryType binary = (BinaryType) left;
            Type leftIntersection = handleIntersection(binary.left, right);
            Type rightIntersection = handleIntersection(binary.right, right);
            return BinaryType.create(leftIntersection, rightIntersection);
        }

        if (left.type == TypeKind.BinaryType && right.type == TypeKind.BinaryType)
        {
            BinaryType leftBin = (BinaryType) left;
            BinaryType rightBin = (BinaryType) right;
            Type ll = handleIntersection(leftBin.left, rightBin.left);
            Type lr = handleIntersection(leftBin.left, rightBin.right);
            Type rl = handleIntersection(leftBin.right, rightBin.left);
            Type rr = handleIntersection(leftBin.right, rightBin.right);
            return BinaryType.create(BinaryType.create(ll, lr), BinaryType.create(rl, rr));
        }

        return NeverType.create();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "underlying: " + left.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        return left + " ou " + right;
    }
}

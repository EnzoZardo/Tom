package Ast.Types.Primitive;

import Entities.Abstractions.Type;
import Entities.Enums.TypeKind;
import Runtime.Environment;

public class NativeFunctionType extends Type
{
    public Type returnType;

    protected NativeFunctionType(Type returnType)
    {
        super(TypeKind.NativeFunction);
        this.returnType = returnType;
    }

    public static NativeFunctionType create(Type returnType)
    {
        return new NativeFunctionType(returnType);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" +
                "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type + ",\n" +
                "\t".repeat(next) + "returnType: " + returnType.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

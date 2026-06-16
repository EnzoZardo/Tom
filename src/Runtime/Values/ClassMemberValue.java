package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Enums.Runtime.ValueType;

public class ClassMemberValue extends RuntimeValue
{
    public final ProtectionLevel protectionLevel;
    public final boolean isStatic;
    public final Type type;

    public ClassValue owner;
    public RuntimeValue value;

    protected ClassMemberValue(
        ProtectionLevel protectionLevel,
        RuntimeValue value,
        ClassValue owner,
        Type type,
        boolean isStatic)
    {
        super(ValueType.ClassMember);
        this.protectionLevel = protectionLevel;
        this.owner = owner;
        this.value = value;
        this.isStatic = isStatic;
        this.type = type;
    }

    public static ClassMemberValue create(
        ProtectionLevel protectionLevel,
        RuntimeValue value,
        ClassValue owner,
        Type type,
        boolean isStatic)
    {
        return new ClassMemberValue(protectionLevel, value, owner, type, isStatic);
    }

    public static RuntimeValue mapToValue(RuntimeValue value)
    {
        if (value.type == ValueType.ClassMember)
        {
            ClassMemberValue member = (ClassMemberValue) value;
            return member.value;
        }

        return value;
    }

    @Override
    public RuntimeValue copy()
    {
        return new ClassMemberValue(protectionLevel, value.copy(), owner, type, isStatic);
    }

    public boolean isPublic()
    {
        return protectionLevel == ProtectionLevel.Public;
    }

    public boolean isPrivate()
    {
        return protectionLevel == ProtectionLevel.Private;
    }

    public boolean isProtected()
    {
        return protectionLevel == ProtectionLevel.Protected;
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "protectionLevel: " + protectionLevel.toString() + ",\n" +
                "\t".repeat(next) + "static: " + isStatic + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        return this.value.equals(that);
    }

    @Override
    public boolean bool()
    {
        return this.value.bool();
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
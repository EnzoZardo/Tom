package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Enums.Runtime.ValueType;

public class ClassMemberValue extends RuntimeValue
{
    public final ProtectionLevel protectionLevel;
    public final String className;
    public RuntimeValue value;

    protected ClassMemberValue(
        ProtectionLevel protectionLevel,
        RuntimeValue value,
        String className)
    {
        super(ValueType.ClassMember);
        this.protectionLevel = protectionLevel;
        this.className = className;
        this.value = value;
    }

    public static ClassMemberValue create(
        ProtectionLevel protectionLevel,
        RuntimeValue value,
        String className)
    {
        return new ClassMemberValue(protectionLevel, value, className);
    }

    public static ClassMemberValue create(
        ProtectionLevel protectionLevel,
        String classValue)
    {
        return new ClassMemberValue(protectionLevel, null, classValue);
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
}
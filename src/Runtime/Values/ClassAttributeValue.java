package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ProtectionLevel;
import Entities.Enums.Runtime.ValueType;

public class ClassAttributeValue extends RuntimeValue
{
    public ProtectionLevel protectionLevel;
    public RuntimeValue value;

    protected ClassAttributeValue(
        ProtectionLevel protectionLevel,
        RuntimeValue value)
    {
        super(ValueType.ClassMember);
        this.protectionLevel = protectionLevel;
        this.value = value;
    }

    public static ClassAttributeValue create(
        ProtectionLevel protectionLevel,
        RuntimeValue value)
    {
        return new ClassAttributeValue(protectionLevel, value);
    }

    public static ClassAttributeValue create(
        ProtectionLevel protectionLevel)
    {
        return new ClassAttributeValue(protectionLevel, null);
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
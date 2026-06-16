package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;

import java.util.HashMap;
import java.util.Map;

public class ClassValue extends RuntimeValue
{
    public final String className;
    public HashMap<String, ClassMemberValue> members;
    public ClassValue parent;
    public final boolean isInstance;

    protected ClassValue(
        ClassValue parent,
        HashMap<String, ClassMemberValue> members,
        String className,
        boolean isInstance)
    {
        super(ValueType.Class);
        this.parent = parent;
        this.members = members;
        this.className = className;
        this.isInstance = isInstance;
    }

    public static ClassValue create(
        String className,
        ClassValue parent,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance)
    {
        return new ClassValue(parent, members, className, isInstance);
    }

    public static ClassValue create(
        String className,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance)
    {
        return new ClassValue(null, members, className, isInstance);
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[\n");
        for (Map.Entry<String, ClassMemberValue> entry : members.entrySet())
        {
            ret.repeat("\t", next)
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().print(next))
                    .append(',')
                    .append('\n');
        }
        return ret.repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "members: " + printProps(next) + ",\n" +
                "\t".repeat(next) + "name: " + className + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        return false;
    }

    @Override
    public boolean bool()
    {
        return true;
    }

    @Override
    public ClassValue copy()
    {
        ClassValue classValue = new ClassValue(
                parent == null ? null : parent.copy(),
                new HashMap<>(),
                className,
                false
        );

        HashMap<String, ClassMemberValue> copiedMembers = new HashMap<>();

        for (Map.Entry<String, ClassMemberValue> entry : members.entrySet())
        {
            ClassMemberValue member = (ClassMemberValue) entry.getValue().copy();
            member.owner = classValue;
            copiedMembers.put(entry.getKey(), member);
        }

        classValue.members = copiedMembers;
        return classValue;
    }
}

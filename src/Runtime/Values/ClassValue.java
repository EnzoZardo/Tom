package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ValueType;
import Ast.Types.SymbolType;
import Entities.Metadata.ArgumentMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassValue extends RuntimeValue
{
    public final String className;
    public HashMap<String, ClassMemberValue> members;
    public ClassValue parent;
    public final boolean isInstance;
    public final boolean isAbstract;
    public final ArrayList<String> typeParameters;
    public ArrayList<Type> typeArguments;
    public ArrayList<Type> parentTypeArguments;

    protected ClassValue(
        ClassValue parent,
        HashMap<String, ClassMemberValue> members,
        String className,
        boolean isInstance,
        boolean isAbstract,
        ArrayList<String> typeParameters,
        ArrayList<Type> typeArguments)
    {
        super(ValueType.Class);
        this.parent = parent;
        this.members = members;
        this.className = className;
        this.isInstance = isInstance;
        this.isAbstract = isAbstract;
        this.typeParameters = typeParameters;
        this.typeArguments = typeArguments;
        this.parentTypeArguments = new ArrayList<>();
    }

    public void setParentTypeArguments(ArrayList<Type> parentTypeArguments)
    {
        this.parentTypeArguments = parentTypeArguments;
    }

    public static ClassValue create(
        String className,
        ClassValue parent,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance,
        boolean isAbstract)
    {
        return new ClassValue(parent, members, className, isInstance, isAbstract, new ArrayList<>(), new ArrayList<>());
    }

    public static ClassValue create(
        String className,
        ClassValue parent,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance,
        boolean isAbstract,
        ArrayList<String> typeParameters)
    {
        return new ClassValue(parent, members, className, isInstance, isAbstract, typeParameters, new ArrayList<>());
    }

    public static ClassValue create(
        String className,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance,
        boolean isAbstract)
    {
        return new ClassValue(null, members, className, isInstance, isAbstract, new ArrayList<>(), new ArrayList<>());
    }

    public static ClassValue create(
        String className,
        HashMap<String, ClassMemberValue> members,
        boolean isInstance,
        boolean isAbstract,
        ArrayList<String> typeParameters)
    {
        return new ClassValue(null, members, className, isInstance, isAbstract, typeParameters, new ArrayList<>());
    }

    public void bindTypeArguments(ArrayList<Type> arguments)
    {
        if (arguments.size() != typeParameters.size())
        {
            throw new RuntimeException(String.format(
                "A classe %s esperava %d argumento(s) de tipo, mas recebeu %d.",
                className,
                typeParameters.size(),
                arguments.size()));
        }

        HashMap<String, Type> mapping = new HashMap<>();
        for (int i = 0; i < typeParameters.size(); i++)
        {
            mapping.put(typeParameters.get(i), arguments.get(i));
        }

        for (ClassMemberValue member : members.values())
        {
            if (member.type != null)
            {
                member.type = SymbolType.substitute(member.type, mapping);
            }

            if (member.value != null && member.value.type == ValueType.Function)
            {
                FunctionValue function = (FunctionValue) member.value;

                ArrayList<ArgumentMetadata> newParameters = new ArrayList<>();
                for (ArgumentMetadata param : function.parameters)
                {
                    newParameters.add(ArgumentMetadata.create(
                        param.getType() == null ? null : SymbolType.substitute(param.getType(), mapping),
                        param.getName()));
                }
                function.parameters = newParameters;

                if (function.returnType != null)
                {
                    function.returnType = SymbolType.substitute(function.returnType, mapping);
                }
            }
        }

        if (parent != null)
        {
            ArrayList<Type> parentArgs = new ArrayList<>();
            for (Type argument : parentTypeArguments)
                parentArgs.add(SymbolType.substitute(argument, mapping));
            parent.bindTypeArguments(parentArgs);
        }

        typeArguments = new ArrayList<>(arguments);
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
                false,
                isAbstract,
                typeParameters,
                new ArrayList<>()
        );
        classValue.parentTypeArguments = new ArrayList<>(parentTypeArguments);

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

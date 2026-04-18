package Runtime.Values;

import Entities.Abstractions.Type;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends RuntimeValue
{
    public HashMap<String, RuntimeValue> properties;

    protected ObjectValue(HashMap<String, RuntimeValue> properties)
    {
        super(ValueType.Object);
        this.properties = properties;
    }

    public static ObjectValue create(HashMap<String, RuntimeValue> properties)
    {
        return new ObjectValue(properties);
    }

    public static ObjectValue create()
    {
        return new ObjectValue(new HashMap<>());
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[\n");
        for (Map.Entry<String, RuntimeValue> entry : properties.entrySet())
        {
            ret.repeat("\t", next)
                .append(entry.getKey())
                .append(": ")
                .append(entry.getValue().print(next))
                .append(',')
                .append('\n');
        }
        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        if (that.type != type) {
            return false;
        }

        ObjectValue objectValue = (ObjectValue) that;

        if (properties.size() != objectValue.properties.size())
        {
            return false;
        }

        for (int i = 0; i < properties.size(); i++)
        {
            if (!properties.get(i).equals(objectValue.properties.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "properties: " + printProps(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean bool()
    {
        final boolean hasProp = !this.properties.isEmpty();
        final boolean notAllNullProps = !this.properties.values().stream().allMatch(x -> x.type == ValueType.Null);
        return hasProp && notAllNullProps;
    }

    @Override
    public boolean not()
    {
        final boolean hasNoProp = this.properties.isEmpty();
        final boolean allNullProps = this.properties.values().stream().allMatch(x -> x.type == ValueType.Null);
        return hasNoProp || allNullProps;
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder("{ ");

        for (Map.Entry<String, RuntimeValue> entry : properties.entrySet())
        {
            ret.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }

        return ret.append("}").toString();
    }
}

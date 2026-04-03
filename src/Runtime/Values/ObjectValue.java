package Runtime.Values;

import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;

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
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "properties: " + printProps(next) + ",\n" +
                "\t".repeat(level) + "}";
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

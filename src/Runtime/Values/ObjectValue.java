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
        StringBuilder ret = new StringBuilder("\n")
                .append("\t".repeat(level))
                .append("[\n");
        for (Map.Entry<String, RuntimeValue> entry : properties.entrySet())
        {
            ret.append("\t".repeat(level + 1))
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue()
                            .print(level + 1))
                    .append(',');
        }
        return ret.append("\n")
                .append("\t".repeat(level))
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "properties: " + printProps(level + 1) + ",\n" +
                "\t".repeat(level) + "}";
    }
}

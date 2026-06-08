package Runtime.Values;

import Entities.Abstractions.Runtime.FreezableValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.InvalidIndexException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectValue extends FreezableValue
{
    public HashMap<String, RuntimeValue> properties;

    protected ObjectValue(HashMap<String, RuntimeValue> properties, boolean frozen)
    {
        super(ValueType.Object, frozen);
        this.properties = properties;
    }

    public static ObjectValue create(HashMap<String, RuntimeValue> properties)
    {
        return new ObjectValue(properties, false);
    }

    public static ObjectValue createFreeze(HashMap<String, RuntimeValue> properties)
    {
        return new ObjectValue(properties, true);
    }

    public static ObjectValue create()
    {
        return new ObjectValue(new HashMap<>(), false);
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

        for (String key : properties.keySet())
        {
            RuntimeValue value = objectValue.properties.get(key);

            if (value == null)
            {
                return false;
            }

            if (!properties.get(key).equals(value))
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
    public RuntimeValue iterate(int index)
    {
        if (index < 0 || index >= properties.size())
        {
            throw new InvalidIndexException("O índice " + index + " é inválido.");
        }

        List<Map.Entry<String, RuntimeValue>> arr = properties.entrySet().stream().toList();
        HashMap<Integer, RuntimeValue> entry = new HashMap<>() {{
            put(0, StringValue.create(arr.get(index).getKey()));
            put(1, arr.get(index).getValue());
        }};

        return ArrayValue.create(entry);
    }

    @Override
    public int iteratorSize()
    {
        return properties.size();
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder("{ ");

        int index = 0;
        for (Map.Entry<String, RuntimeValue> entry : properties.entrySet())
        {
            ret.append(entry.getKey()).append(": ").append(entry.getValue());
            if (index < properties.size() - 1) {
                ret.append(", ");
            }
            index++;
        }

        return ret.append(" }").toString();
    }

    @Override
    public RuntimeValue copy()
    {
        HashMap<String, RuntimeValue> copiedProperties = new HashMap<>();

        for (Map.Entry<String, RuntimeValue> entry : properties.entrySet())
        {
            copiedProperties.put(
                    entry.getKey(),
                    entry.getValue().copy()
            );
        }

        return new ObjectValue(copiedProperties, frozen);
    }
}

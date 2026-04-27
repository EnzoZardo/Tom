package Runtime.Values;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.InvalidIndexException;

import java.util.HashMap;
import java.util.Map;

public class ArrayValue extends RuntimeValue
{
    public HashMap<Integer, RuntimeValue> items;

    protected ArrayValue(HashMap<Integer, RuntimeValue> items)
    {
        super(ValueType.Array);
        this.items = items;
    }

    public static ArrayValue create(HashMap<Integer, RuntimeValue> items)
    {
        return new ArrayValue(items);
    }

    public static ArrayValue create()
    {
        return new ArrayValue(new HashMap<>());
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
                .repeat("\t", level)
                .append("[\n");
        for (Map.Entry<Integer, RuntimeValue> entry : items.entrySet())
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

        ArrayValue arrayValue = (ArrayValue) that;

        if (arrayValue.items.size() != items.size())
        {
            return false;
        }

        for (int i = 0; i < items.size(); i++)
        {
            if (!items.get(i).equals(arrayValue.items.get(i)))
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
                "\t".repeat(next) + "items: " + printProps(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean bool()
    {
        return !this.items.isEmpty();
    }

    @Override
    public RuntimeValue iterate(int index)
    {
        if (index < 0 || index >= items.size())
        {
            throw new InvalidIndexException("O índice " + index + " é inválido.");
        }

        return items.get(index);
    }

    @Override
    public int iteratorSize()
    {
        return items.size();
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder("[");

        for (int i = 0; i < items.size(); i++)
        {
            ret.append(items.get(i));
            if (i < items.size() - 1)
            {
                ret.append(", ");
            }
        }

        return ret.append("]").toString();
    }
}

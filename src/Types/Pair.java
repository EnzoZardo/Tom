package Types;

public class Pair<TFirst, TSecond> {
    protected final TFirst first;
    protected final TSecond second;

    protected Pair(TFirst first, TSecond second)
    {
        this.first = first;
        this.second = second;
    }

    public TFirst get0() {
        return first;
    }

    public TSecond get1() {
        return second;
    }

    public static <TFirst, TSecond> Pair<TFirst, TSecond> create(TFirst first, TSecond second)
    {
        return new Pair<>(first, second);
    }
}


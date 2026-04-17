package Entities.Common;

public class Pair<TFirst, TSecond> {
    protected final TFirst first;
    protected final TSecond second;

    protected Pair(TFirst first, TSecond second)
    {
        this.first = first;
        this.second = second;
    }

    protected TFirst get0() {
        return first;
    }

    protected TSecond get1() {
        return second;
    }
}


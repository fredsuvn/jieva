package xyz.srclab.common.util.count;

/**
 * @author sunqian
 */
final class CounterImpl implements Counter {

    private long value;

    CounterImpl() {
        this(0);
    }

    CounterImpl(long value) {
        this.value = value;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public void setLong(long l) {
        value = l;
    }

    @Override
    public long addAndGetLong(long l) {
        value += l;
        return value;
    }

    @Override
    public long getLongAndAdd(long l) {
        long temp = value;
        value += l;
        return temp;
    }

    @Override
    public long getLongAndIncrement() {
        return value++;
    }

    @Override
    public long incrementAndGetLong() {
        return ++value;
    }
}

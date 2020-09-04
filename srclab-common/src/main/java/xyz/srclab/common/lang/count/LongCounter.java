package xyz.srclab.common.lang.count;

public interface LongCounter {

    static LongCounter fromZero() {
        return LongCounterSupport.newSimpleLongCounter();
    }

    static LongCounter from(long value) {
        return LongCounterSupport.newSimpleLongCounter(value);
    }

    static LongCounter atomic() {
        return LongCounterSupport.newAtomicLongCounter();
    }

    static LongCounter atomic(long value) {
        return LongCounterSupport.newAtomicLongCounter(value);
    }

    long get();

    void set(long value);

    default void add(long value) {
        set(get() + value);
    }

    default long getAndSet(long value) {
        long result = get();
        set(value);
        return result;
    }

    default long getAndAdd(long value) {
        long result = get();
        set(result + value);
        return result;
    }

    default long addAndGet(long value) {
        add(value);
        return get();
    }

    default long getAndIncrement() {
        return getAndAdd(1);
    }

    default long incrementAndGet() {
        return addAndGet(1);
    }

    default long getAndDecrement() {
        return getAndAdd(-1);
    }

    default long decrementAndGet() {
        return addAndGet(-1);
    }

    default void clear() {
        set(0);
    }
}

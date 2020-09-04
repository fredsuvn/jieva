package xyz.srclab.common.lang.count;

public interface IntCounter {

    static IntCounter zero() {
        return IntCounterSupport.newSimpleIntCounter();
    }

    static IntCounter from(int init) {
        return IntCounterSupport.newSimpleIntCounter(init);
    }

    static IntCounter atomicZero() {
        return IntCounterSupport.newAtomicIntCounter();
    }

    static IntCounter atomic(int init) {
        return IntCounterSupport.newAtomicIntCounter(init);
    }

    int get();

    void set(int value);

    default void add(int value) {
        set(get() + value);
    }

    default int getAndSet(int value) {
        int result = get();
        set(value);
        return result;
    }

    default int getAndAdd(int value) {
        int result = get();
        set(result + value);
        return result;
    }

    default int addAndGet(int value) {
        add(value);
        return get();
    }

    default int getAndIncrement() {
        return getAndAdd(1);
    }

    default int incrementAndGet() {
        return addAndGet(1);
    }

    default int getAndDecrement() {
        return getAndAdd(-1);
    }

    default int decrementAndGet() {
        return addAndGet(-1);
    }

    default void clear() {
        set(0);
    }
}

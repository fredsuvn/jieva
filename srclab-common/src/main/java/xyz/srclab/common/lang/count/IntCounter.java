package xyz.srclab.common.lang.count;

public interface IntCounter {

    static IntCounter fromZero() {
        return IntCounterSupport.newSimpleIntCounter();
    }

    static IntCounter from(int value) {
        return IntCounterSupport.newSimpleIntCounter(value);
    }

    int get();

    void set(int value);

    default void add(int addend) {
        set(get() + addend);
    }

    default int getAndSet(int value) {
        int result = get();
        set(value);
        return result;
    }

    default int getAndAdd(int delta) {
        int result = get();
        add(delta);
        return result;
    }

    default int addAndGet(int delta) {
        add(delta);
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

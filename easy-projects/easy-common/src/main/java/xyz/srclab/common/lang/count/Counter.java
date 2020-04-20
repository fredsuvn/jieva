package xyz.srclab.common.lang.count;

/**
 * @author sunqian
 */
public interface Counter {

    static Counter withZero() {
        return new CounterImpl();
    }

    static Counter with(long value) {
        return new CounterImpl(value);
    }

    default int getInt() {
        return (int) getLong();
    }

    long getLong();

    default void setInt(int i) {
        setLong(i);
    }

    void setLong(long l);

    default int addAndGetInt(int i) {
        return (int) addAndGetLong(i);
    }

    long addAndGetLong(long l);

    default int getIntAndAdd(int i) {
        return (int) getLongAndAdd(i);
    }

    long getLongAndAdd(long l);

    default int getIntAndIncrement() {
        return (int) getLongAndIncrement();
    }

    long getLongAndIncrement();

    default int incrementAndGetInt() {
        return (int) incrementAndGetLong();
    }

    long incrementAndGetLong();
}

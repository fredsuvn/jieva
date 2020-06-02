package xyz.srclab.common.util.count;

/**
 * @author sunqian
 */
public interface Counter {

    static Counter fromZero() {
        return Count0.newCounter();
    }

    static Counter from(long value) {
        return Count0.newCounter(value);
    }

    static Counter fromZeroThreadSafe() {
        return Count0.newThreadSafeCounter();
    }

    static Counter fromThreadSafe(long value) {
        return Count0.newThreadSafeCounter(value);
    }

    default int getInt() {
        return (int) getLong();
    }

    long getLong();

    default void setInt(int value) {
        setLong(value);
    }

    void setLong(long value);

    default int addAndGetInt(int value) {
        return (int) addAndGetLong(value);
    }

    long addAndGetLong(long value);

    default int getIntAndAdd(int value) {
        return (int) getLongAndAdd(value);
    }

    long getLongAndAdd(long value);

    default int getIntAndIncrement() {
        return (int) getLongAndIncrement();
    }

    long getLongAndIncrement();

    default int incrementAndGetInt() {
        return (int) incrementAndGetLong();
    }

    long incrementAndGetLong();
}

package xyz.srclab.common.base;

/**
 * @author sunqian
 */
public class Counter {

    private long value;

    public Counter() {
        this(0);
    }

    public Counter(int init) {
        this((long) init);
    }

    public Counter(long init) {
        this.value = init;
    }

    public int getInt() {
        return (int) getLong();
    }

    public long getLong() {
        return value;
    }

    public void set(int i) {
        set((long) i);
    }

    public void set(long l) {
        this.value = l;
    }

    public int addAndGetInt(int i) {
        return (int) addAndGetLong(i);
    }

    public long addAndGetLong(long l) {
        value += l;
        return value;
    }

    public int getIntAndAdd(int i) {
        return (int) getLongAndAdd(i);
    }

    public long getLongAndAdd(long l) {
        long temp = value;
        value += l;
        return temp;
    }

    public int getIntAndIncrement() {
        return (int) getLongAndIncrement();
    }

    public long getLongAndIncrement() {
        return value++;
    }

    public int incrementAndGetInt() {
        return (int) incrementAndGetLong();
    }

    public long incrementAndGetLong() {
        return ++value;
    }
}

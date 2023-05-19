package xyz.srclab.common.base.ref;

/**
 * {@link Ref} for long type.
 *
 * @author fredsuvn
 */
public class LongRef {

    private long value;

    /**
     * Constructs with 0.
     */
    public LongRef() {
        this(0);
    }

    /**
     * Constructs with given value.
     *
     * @param value given value
     */
    public LongRef(long value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public long get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(long value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public long incrementAndGet() {
        return ++value;
    }

    /**
     * Adds specified number for current value then return:
     *
     * <pre>
     *     value += addon;
     *     return value;
     * </pre>
     *
     * @param addon specified number
     */
    public long incrementAndGet(long addon) {
        value += addon;
        return value;
    }

    /**
     * Returns current value then adds 1 for the value:
     *
     * <pre>
     *     return value++;
     * </pre>
     */
    public long getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     long temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public long getAndIncrement(long addon) {
        long temp = value;
        value += addon;
        return temp;
    }
}

package xyz.fsgek.common.base.ref;

/**
 * {@link FsRef} for long type.
 *
 * @author fredsuvn
 */
public class LongRef {

    private long value;

    LongRef(long value) {
        this.value = value;
    }

    /**
     * Returns value of this ref.
     *
     * @return value of this ref
     */
    public long get() {
        return value;
    }

    /**
     * Sets value of this ref.
     *
     * @param value value of this ref
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
     *
     * @return ++value
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
     * @return value += addon
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
     *
     * @return value++
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
     * @return current value
     */
    public long getAndIncrement(long addon) {
        long temp = value;
        value += addon;
        return temp;
    }
}

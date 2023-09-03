package xyz.srclab.common.base.ref;

/**
 * {@link FsRef} for int type.
 *
 * @author fredsuvn
 */
public class IntRef {

    private int value;

    IntRef(int value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public int get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(int value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public int incrementAndGet() {
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
    public int incrementAndGet(int addon) {
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
    public int getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     int temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public int getAndIncrement(int addon) {
        int temp = value;
        value += addon;
        return temp;
    }
}

package xyz.srclab.common.base.ref;

/**
 * {@link FsRef} for short type.
 *
 * @author fredsuvn
 */
public class ShortRef {

    private short value;

    ShortRef(short value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public short get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(short value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public short incrementAndGet() {
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
    public short incrementAndGet(short addon) {
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
    public short getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     short temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public short getAndIncrement(short addon) {
        short temp = value;
        value += addon;
        return temp;
    }
}

package xyz.fsgek.common.base.ref;

/**
 * {@link GekRef} for byte type.
 *
 * @author fredsuvn
 */
public class ByteRef {

    private byte value;

    ByteRef(byte value) {
        this.value = value;
    }

    /**
     * Returns value of this ref.
     *
     * @return value of this ref
     */
    public byte get() {
        return value;
    }

    /**
     * Sets value of this ref.
     *
     * @param value value of this ref
     */
    public void set(byte value) {
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
    public byte incrementAndGet() {
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
    public byte incrementAndGet(byte addon) {
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
    public byte getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     byte temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     * @return current value
     */
    public byte getAndIncrement(byte addon) {
        byte temp = value;
        value += addon;
        return temp;
    }
}

package xyz.srclab.common.base.ref;

/**
 * {@link Ref} for byte type.
 *
 * @author fredsuvn
 */
public class ByteRef {

    private byte value;

    /**
     * Constructs with 0.
     */
    public ByteRef() {
        this((byte) 0);
    }

    /**
     * Constructs with given value.
     *
     * @param value given value
     */
    public ByteRef(byte value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public byte get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
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
     */
    public byte getAndIncrement(byte addon) {
        byte temp = value;
        value += addon;
        return temp;
    }
}

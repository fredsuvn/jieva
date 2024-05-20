package xyz.fsgek.common.base.ref;

/**
 * {@link GekRef} for byte type.
 *
 * @author fredsuvn
 */
public interface ByteRef {

    /**
     * Returns an instance of {@link ByteRef} of 0 initialized value.
     *
     * @return an instance of {@link ByteRef} of 0 initialized value
     */
    static ByteRef ofZero() {
        return of((byte) 0);
    }

    /**
     * Returns an instance of {@link ByteRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteRef} of initialized value
     */
    static ByteRef of(byte value) {
        return Impls.newByteRef(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    byte get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(byte value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    byte incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    byte getAndIncrement();

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    byte add(byte v);
}

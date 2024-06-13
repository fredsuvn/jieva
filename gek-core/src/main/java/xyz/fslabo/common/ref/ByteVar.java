package xyz.fslabo.common.ref;

/**
 * Byte version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface ByteVar {

    /**
     * Returns an instance of {@link ByteVar} of 0 initialized value.
     *
     * @return an instance of {@link ByteVar} of 0 initialized value
     */
    static ByteVar ofZero() {
        return of((byte) 0);
    }

    /**
     * Returns an instance of {@link ByteVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteVar} of initialized value
     */
    static ByteVar of(byte value) {
        return VarImpls.newByteVar(value);
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

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Byte> toVar() {
        return Var.of(get());
    }
}

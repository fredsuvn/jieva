package xyz.fslabo.common.ref;

/**
 * Byte version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface ByteVar extends ByteVal {

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
        return VarImpls.ofByte(value);
    }

    /**
     * Sets and returns specified value.
     *
     * @param value specified value
     * @return specified value
     */
    byte set(byte value);

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param value specified value
     * @return the result
     */
    byte add(byte value);

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
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Byte> toWrapper() {
        return Var.of(get());
    }
}

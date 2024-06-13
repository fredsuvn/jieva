package xyz.fslabo.common.ref;

/**
 * Short version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface ShortVar {

    /**
     * Returns an instance of {@link ShortVar} of 0 initialized value.
     *
     * @return an instance of {@link ShortVar} of 0 initialized value
     */
    static ShortVar ofZero() {
        return of((short) 0);
    }

    /**
     * Returns an instance of {@link ShortVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortVar} of initialized value
     */
    static ShortVar of(short value) {
        return VarImpls.newShortVar(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    short get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(short value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    short incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    short getAndIncrement();

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    short add(short v);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Short> toVar() {
        return Var.of(get());
    }
}

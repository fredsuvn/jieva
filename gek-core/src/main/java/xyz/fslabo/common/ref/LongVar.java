package xyz.fslabo.common.ref;

/**
 * Long version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface LongVar {

    /**
     * Returns an instance of {@link LongVar} of 0 initialized value.
     *
     * @return an instance of {@link LongVar} of 0 initialized value
     */
    static LongVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link LongVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link LongVar} of initialized value
     */
    static LongVar of(long value) {
        return VarImpls.newLongVar(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    long get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(long value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    long incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    long getAndIncrement();

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    long add(long v);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Long> toVar() {
        return Var.of(get());
    }
}

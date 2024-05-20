package xyz.fsgek.common.base.ref;

/**
 * {@link GekRef} for long type.
 *
 * @author fredsuvn
 */
public interface LongRef {

    /**
     * Returns an instance of {@link LongRef} of 0 initialized value.
     *
     * @return an instance of {@link LongRef} of 0 initialized value
     */
    static LongRef ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link LongRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link LongRef} of initialized value
     */
    static LongRef of(long value) {
        return Impls.newLongRef(value);
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
}

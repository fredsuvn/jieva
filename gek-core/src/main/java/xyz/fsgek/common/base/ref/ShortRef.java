package xyz.fsgek.common.base.ref;

/**
 * {@link GekRef} for short type.
 *
 * @author fredsuvn
 */
public interface ShortRef {

    /**
     * Returns an instance of {@link ShortRef} of 0 initialized value.
     *
     * @return an instance of {@link ShortRef} of 0 initialized value
     */
    static ShortRef ofZero() {
        return of((short) 0);
    }

    /**
     * Returns an instance of {@link ShortRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortRef} of initialized value
     */
    static ShortRef of(short value) {
        return Impls.newShortRef(value);
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
}

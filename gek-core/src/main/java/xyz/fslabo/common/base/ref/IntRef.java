package xyz.fslabo.common.base.ref;

/**
 * {@link GekRef} for int type.
 *
 * @author fredsuvn
 */
public interface IntRef {

    /**
     * Returns an instance of {@link IntRef} of 0 initialized value.
     *
     * @return an instance of {@link IntRef} of 0 initialized value
     */
    static IntRef ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link IntRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link IntRef} of initialized value
     */
    static IntRef of(int value) {
        return Impls.newIntRef(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    int get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(int value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    int incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    int getAndIncrement();

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    int add(int v);
}

package xyz.fslabo.common.base.ref;

/**
 * {@link GekRef} for boolean type.
 *
 * @author fredsuvn
 */
public interface BooleanRef {

    /**
     * Returns an instance of {@link BooleanRef} of false initialized value.
     *
     * @return an instance of {@link BooleanRef} of false initialized value
     */
    static BooleanRef ofFalse() {
        return of(false);
    }

    /**
     * Returns an instance of {@link BooleanRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link BooleanRef} of initialized value
     */
    static BooleanRef of(boolean value) {
        return Impls.newBooleanRef(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    boolean get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(boolean value);

    /**
     * Toggles current value, and returns the result.
     *
     * @return toggle result
     */
    boolean toggleAndGet();

    /**
     * Toggles current value, and returns the old value before toggling.
     *
     * @return old value before toggling
     */
    boolean getAndToggle();
}

package xyz.srclab.common.base.ref;

/**
 * {@link Ref} for boolean type.
 *
 * @author fredsuvn
 */
public class BooleanRef {

    private boolean value;

    /**
     * Constructs with false.
     */
    public BooleanRef() {
        this(false);
    }

    /**
     * Constructs with given value.
     *
     * @param value given value
     */
    public BooleanRef(boolean value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public boolean get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(boolean value) {
        this.value = value;
    }
}

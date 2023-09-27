package xyz.fs404.common.base.ref;

/**
 * {@link FsRef} for boolean type.
 *
 * @author fredsuvn
 */
public class BooleanRef {

    private boolean value;

    BooleanRef(boolean value) {
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

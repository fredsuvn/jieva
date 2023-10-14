package xyz.fsgik.common.base.ref;

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
     * Returns value of this ref.
     *
     * @return value of this ref
     */
    public boolean get() {
        return value;
    }

    /**
     * Sets value of this ref.
     *
     * @param value value of this ref
     */
    public void set(boolean value) {
        this.value = value;
    }
}

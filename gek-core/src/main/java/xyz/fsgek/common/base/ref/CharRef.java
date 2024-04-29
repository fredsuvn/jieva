package xyz.fsgek.common.base.ref;

/**
 * {@link GekRef} for char type.
 *
 * @author fredsuvn
 */
public interface CharRef {

    /**
     * Returns an instance of {@link CharRef} of 0 initialized value.
     *
     * @return an instance of {@link CharRef} of 0 initialized value
     */
    static CharRef ofZero() {
        return of((char) 0);
    }

    /**
     * Returns an instance of {@link CharRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharRef} of initialized value
     */
    static CharRef of(char value) {
        return RefImpls.newCharRef(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    char get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(char value);
}

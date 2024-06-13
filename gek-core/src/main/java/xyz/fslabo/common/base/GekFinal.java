package xyz.fslabo.common.base;

/**
 * Abstract class caches the results of hashCode and toString methods.
 * This class is used to extend for immutable type of which hashCode and toString methods will be used frequently
 * (and its results are always same).
 * <p>
 * If you extend this class, you just need to override {@link #computeHashCode()} and {@link #computeToString()}
 * (rather than {@link #hashCode()} and {@link #toString()}) to compute results of hashCode and toString,
 * no need to override {@link #hashCode()} and {@link #toString()}.
 *
 * @author fredsuvn
 */
public abstract class GekFinal {

    private int hash;
    private String toString;

    /**
     * Computes {@link Object#hashCode()} of this object.
     *
     * @return computed hash code
     */
    protected abstract int computeHashCode();

    /**
     * Computes {@link Object#toString()} of this object.
     *
     * @return computed string
     */
    protected abstract String computeToString();

    @Override
    public int hashCode() {
        if (hash != 0) {
            return hash;
        }
        synchronized (this) {
            if (hash != 0) {
                return hash;
            }
            int newHash = computeHashCode();
            if (newHash == 0) {
                newHash = 1;
            }
            hash = newHash;
            return newHash;
        }
    }

    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }
        synchronized (this) {
            if (toString != null) {
                return toString;
            }
            toString = computeToString();
            return toString;
        }
    }
}

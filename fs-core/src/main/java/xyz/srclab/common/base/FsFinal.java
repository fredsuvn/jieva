package xyz.srclab.common.base;

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
public abstract class FsFinal {

    private Integer hash;
    private String toString;

    /**
     * Computes hashCode of this object.
     */
    protected abstract int computeHashCode();

    /**
     * Computes toString of this object.
     */
    protected abstract String computeToString();

    @Override
    public int hashCode() {
        if (hash != null) {
            return hash;
        }
        synchronized (this) {
            if (hash != null) {
                return hash;
            }
            int newHash = computeHashCode();
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

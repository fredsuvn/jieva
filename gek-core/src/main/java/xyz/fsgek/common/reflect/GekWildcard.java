package xyz.fsgek.common.reflect;

import xyz.fslabo.annotations.Nullable;
import xyz.fsgek.common.collect.GekColl;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * This interface is sub and enhanced type of {@link WildcardType}.
 *
 * @author fredsuvn
 */
public interface GekWildcard extends WildcardType {

    /**
     * Returns immutable list of {@link #getUpperBounds()}.
     *
     * @return immutable list of {@link #getUpperBounds()}
     */
    List<Type> getUpperBoundList();

    /**
     * Returns upper bound (? extends) of this wildcard, or null if it doesn't have.
     *
     * @return upper bound (? extends) of this wildcard, or null if it doesn't have
     */
    @Nullable
    default Type getUpperBound() {
        List<Type> uppers = getUpperBoundList();
        if (GekColl.isNotEmpty(uppers)) {
            return uppers.get(0);
        }
        return null;
    }

    /**
     * Returns immutable list of {@link #getLowerBounds()}.
     *
     * @return immutable list of {@link #getLowerBounds()}
     */
    List<Type> getLowerBoundList();

    /**
     * Returns lower bound (? super) of this wildcard, or null if it doesn't have.
     *
     * @return lower bound (? super) of this wildcard, or null if it doesn't have
     */
    @Nullable
    default Type getLowerBound() {
        List<Type> lowers = getLowerBoundList();
        if (GekColl.isNotEmpty(lowers)) {
            return lowers.get(0);
        }
        return null;
    }
}

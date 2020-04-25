package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Immutable;

/**
 * @author sunqian
 */
@Immutable
public interface Key {

    static Key from(Object... keyComponents) {
        return KeySupport.buildCacheKey(keyComponents);
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);
}

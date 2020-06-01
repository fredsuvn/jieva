package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
@Immutable
public interface Key {

    static Key of(Object... elements) {
        return Key0.newKey(elements);
    }

    static Key of(Iterable<?> elements) {
        return Key0.newKey(elements);
    }

    @Override
    int hashCode();

    @Override
    boolean equals(@Nullable Object other);

    @Override
    String toString();
}

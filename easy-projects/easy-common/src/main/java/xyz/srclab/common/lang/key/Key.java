package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Immutable;

import java.util.List;

/**
 * @author sunqian
 */
@Immutable
public interface Key {

    static Key from(Object... keyComponents) {
        return KeySupport.buildCacheKey(keyComponents);
    }

    @Immutable
    List<Object> getKeyComponents();
}

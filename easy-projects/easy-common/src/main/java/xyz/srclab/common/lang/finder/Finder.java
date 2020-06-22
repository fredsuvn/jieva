package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

public interface Finder<K, V> {

    @Nullable
    V get(K key);
}

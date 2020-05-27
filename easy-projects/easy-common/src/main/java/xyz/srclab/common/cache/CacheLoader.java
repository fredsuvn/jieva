package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    static <K, V> Builder<K, V> forKey(K key) {
        return new Builder<>(key);
    }

    @Nullable
    CacheExpiry getExpiry();

    @Nullable
    V load(K key);

    default Map<K, @Nullable V> loadAll(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, load(key));
        }
        return result;
    }

    final class Builder<K, V> extends CachedBuilder<CacheLoader<K, V>> {

        private final K key;
        private @Nullable CacheExpiry expiry;
        private @Nullable Function<? super K, @Nullable ? extends V> loadFunction;

        public Builder(K key) {
            this.key = key;
        }

        public Builder<K, V> setExpiry(CacheExpiry expiry) {
            this.expiry = expiry;
            this.updateState();
            return this;
        }

        public Builder<K, V> setLoadFunction(Function<? super K, @Nullable ? extends V> loadFunction) {
            this.loadFunction = loadFunction;
            this.updateState();
            return this;
        }

        @Override
        protected CacheLoader<K, V> buildNew() {
            Checker.checkState(loadFunction != null, "Load function should not be null");
            return new CacheLoaderImpl<>(this);
        }

        private static final class CacheLoaderImpl<K, V> implements CacheLoader<K, V> {

            private final K key;
            private final @Nullable CacheExpiry expiry;
            private final Function<? super K, @Nullable ? extends V> loadFunction;

            private CacheLoaderImpl(Builder<K, V> builder) {
                Checker.checkState(builder.loadFunction != null, "Load function should not be null");
                this.key = builder.key;
                this.expiry = builder.expiry;
                this.loadFunction = builder.loadFunction;
            }

            public K getKey() {
                return key;
            }

            @Override
            public @Nullable CacheExpiry getExpiry() {
                return expiry;
            }

            @Override
            public V load(K key) {
                return loadFunction.apply(key);
            }
        }
    }
}

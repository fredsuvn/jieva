package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.cache.listener.CacheListener;
import xyz.srclab.common.collection.list.ListHelper;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public final class CacheBuilder<K, V> {

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    private long maxSize = Long.MAX_VALUE;

    private @Nullable Duration expiryAfterCreate;

    private @Nullable Duration expiryAfterRead;

    private @Nullable Duration expiryAfterUpdate;

    private int concurrencyLevel = Defaults.DEFAULT_CONCURRENCY_LEVEL;

    private final List<CacheListener<K, V>> listeners = new LinkedList<>();

    public CacheBuilder<K, V> setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder<K, V> setExpiryAfterCreate(Duration expiryAfterCreate) {
        this.expiryAfterCreate = expiryAfterCreate;
        return this;
    }

    public CacheBuilder<K, V> setExpiryAfterRead(Duration expiryAfterRead) {
        this.expiryAfterRead = expiryAfterRead;
        return this;
    }

    public CacheBuilder<K, V> setExpiryAfterUpdate(Duration expiryAfterUpdate) {
        this.expiryAfterUpdate = expiryAfterUpdate;
        return this;
    }

    public CacheBuilder<K, V> setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    @SafeVarargs
    public final CacheBuilder<K, V> setListeners(CacheListener<K, V>... listeners) {
        return setListeners(Arrays.asList(listeners));
    }

    public CacheBuilder<K, V> setListeners(Iterable<CacheListener<K, V>> listeners) {
        return this;
    }

    long getMaxSize() {
        return maxSize;
    }

    Duration getExpiryAfterCreate() {
        return expiryAfterCreate == null ? Duration.ZERO : expiryAfterCreate;
    }

    Duration getExpiryAfterRead() {
        return expiryAfterRead == null ? Duration.ZERO : expiryAfterRead;
    }

    Duration getExpiryAfterUpdate() {
        return expiryAfterUpdate == null ? Duration.ZERO : expiryAfterUpdate;
    }

    int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    List<CacheListener<K, V>> getListeners() {
        return ListHelper.immutable(listeners);
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().maximumSize(maxSize);
        if (expiryAfterUpdate != null) {
            caffeine.expireAfterWrite(expiryAfterUpdate);
        }
        if (expiryAfterRead != null) {
            caffeine.expireAfterAccess(expiryAfterRead);
        }
        caffeine.removalListener(new RemovalListener<K, V>() {
            @Override
            public void onRemoval(@org.checkerframework.checker.nullness.qual.Nullable K key, @org.checkerframework.checker.nullness.qual.Nullable V value, @NonNull RemovalCause cause) {

            }
        });
        return new CaffeineCache<>(caffeine.build());
    }
}

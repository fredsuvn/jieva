package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.cache.listener.*;

import java.time.Duration;

/**
 * @author sunqian
 */
public final class CacheBuilder<K, V> {

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    private long maxSize = Long.MAX_VALUE;
    private int concurrencyLevel = Defaults.CONCURRENCY_LEVEL;

    private @Nullable Duration expiryAfterCreate;
    private @Nullable Duration expiryAfterRead;
    private @Nullable Duration expiryAfterUpdate;

    private @Nullable CacheCreateListener<K, V> createListener;
    private @Nullable CacheReadListener<K, V> readListener;
    private @Nullable CacheUpdateListener<K, V> updateListener;
    private @Nullable CacheRemoveListener<K, V> removeListener;

    private boolean useGuava = false;

    public CacheBuilder<K, V> setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder<K, V> setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
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

    public CacheBuilder<K, V> setCreateListener(CacheCreateListener<K, V> createListener) {
        this.createListener = createListener;
        return this;
    }

    public CacheBuilder<K, V> setReadListener(CacheReadListener<K, V> readListener) {
        this.readListener = readListener;
        return this;
    }

    public CacheBuilder<K, V> setUpdateListener(CacheUpdateListener<K, V> updateListener) {
        this.updateListener = updateListener;
        return this;
    }

    public CacheBuilder<K, V> setRemoveListener(CacheRemoveListener<K, V> removeListener) {
        this.removeListener = removeListener;
        return this;
    }

    public CacheBuilder<K, V> useGuava() {
        this.useGuava = true;
        return this;
    }

    public CacheBuilder<K, V> useCaffeine() {
        this.useGuava = false;
        return this;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public Duration getExpiryAfterCreate() {
        return expiryAfterCreate == null ? Duration.ZERO : expiryAfterCreate;
    }

    public Duration getExpiryAfterRead() {
        return expiryAfterRead == null ? Duration.ZERO : expiryAfterRead;
    }

    public Duration getExpiryAfterUpdate() {
        return expiryAfterUpdate == null ? Duration.ZERO : expiryAfterUpdate;
    }

    public CacheCreateListener<K, V> getCreateListener() {
        return createListener == null ? CacheListener.emptyCreateListener() : createListener;
    }

    public CacheReadListener<K, V> getReadListener() {
        return readListener == null ? CacheListener.emptyReadListener() : readListener;
    }

    public CacheUpdateListener<K, V> getUpdateListener() {
        return updateListener == null ? CacheListener.emptyUpdateListener() : updateListener;
    }

    public CacheRemoveListener<K, V> getRemoveListener() {
        return removeListener == null ? CacheListener.emptyRemoveListener() : removeListener;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        Cache<K, V> cache = useGuava ? new GuavaCache<>(this) : new CaffeineCache<>(this);
        return (Cache<K1, V1>) cache;
    }
}

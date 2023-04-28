package xyz.srclab.common.cache

import com.google.common.collect.MapMaker
import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.Val
import xyz.srclab.common.base.defaultConcurrencyLevel
import xyz.srclab.common.cache.Cache.Builder
import java.time.Duration
import java.util.function.Function

/**
 * A simple cache interface, thread-safe, built-in implemented by:
 *
 * * [caffeine](https://github.com/ben-manes/caffeine): [CaffeineCache], [LoadingCaffeineCache];
 * * [guava](https://github.com/google/guava): [GuavaCache], [LoadingGuavaCache];
 * * Map: [MapCache], [LoadingMapCache];
 *
 * To create a [Cache], you can use static methods of [Cache], or [Cache.Builder].
 *
 * A cache may have a default loader ([Cache.Builder.loader]). When use `get` methods,
 * the default loader will be called automatically if the value doesn't exist.
 * If loading failed, the loader will throw a [NoSuchElementException].
 * If a [Cache] has a default loader, we will call it `Loading Cache`.
 *
 * The key must not null but **null value is permitted**.
 *
 * @see Builder
 */
@ThreadSafe
interface Cache<K : Any, V : Any> {

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, and this cache has a default Loader, try to load, cache and return.
     * If the default loader still can't get the value, or this cache doesn't have a default loader,
     * a [NoSuchElementException] will be thrown.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    operator fun get(key: K): V? {
        val cv = getVal(key)
        if (cv === null) {
            throw NoSuchElementException(key.toString())
        }
        return cv.get()
    }

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [CacheVal].
     * If the value is not found in this cache, and this cache has a default Loader, try to load, cache and return.
     * If the default loader still can't get the value, or this cache doesn't have a default loader, return null.
     */
    @Throws(CacheException::class)
    fun getVal(key: K): CacheVal<V>?

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, try to load by [loader], cache and return.
     * If the [loader] still can't get the value, a [NoSuchElementException] will be thrown.
     *
     * Note for [loader] if loading failed, the [loader] will throw a [NoSuchElementException].
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun get(key: K, loader: Function<in K, out V?>): V? {
        val cv = getVal(key, loader)
        if (cv === null) {
            throw NoSuchElementException(key.toString())
        }
        return cv.get()
    }

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [CacheVal].
     * If the value is not found in this cache, try to load by [loader], cache and return.
     * If the [loader] still can't get the value, return null.
     *
     * Note for [loader] if loading failed, the [loader] will throw a [NoSuchElementException].
     */
    @Throws(CacheException::class)
    fun getVal(key: K, loader: Function<in K, out V?>): CacheVal<V>?

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, a [NoSuchElementException] will be thrown.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun getPresent(key: K): V? {
        val cv = getPresentVal(key)
        if (cv === null) {
            throw NoSuchElementException(key.toString())
        }
        return cv.get()
    }

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [CacheVal].
     * If the value is not found in this cache, return null.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class)
    fun getPresentVal(key: K): CacheVal<V>?

    /**
     * Puts the key-value pair into this cache.
     */
    @Throws(CacheException::class)
    fun put(key: K, value: V)

    /**
     * Puts the key-value pair into this cache.
     *
     * This method is a kotlin `operator` method for [put].
     */
    @Throws(CacheException::class)
    operator fun set(key: K, value: V) {
        put(key, value)
    }

    /**
     * Removes the key-value pair from this cache
     */
    @Throws(CacheException::class)
    fun remove(key: K)

    /**
     * Removes all entries from this cache.
     */
    @Throws(CacheException::class)
    fun clear()

    /**
     * Cleans up the cache, may remove the expired data.
     */
    @Throws(CacheException::class)
    fun cleanUp()

    /**
     * Builder for [Cache]. This builder can build [Cache] based on `caffeine`(default) and `guava`.
     */
    class Builder<K : Any, V : Any> {

        var initialCapacity: Int? = null
        var maxCapacity: Long? = null
        var concurrencyLevel: Int? = null
        var expireAfterAccess: Duration? = null
        var expireAfterWrite: Duration? = null
        var refreshAfterWrite: Duration? = null
        var loader: Function<in K, out V>? = null
        var useGuava = false

        /**
         * Sets initial capacity.
         */
        fun initialCapacity(initialCapacity: Int): Builder<K, V> = apply {
            this.initialCapacity = initialCapacity
        }

        /**
         * Sets max capacity.
         */
        fun maxCapacity(maxCapacity: Long): Builder<K, V> = apply {
            this.maxCapacity = maxCapacity
        }

        /**
         * Sets concurrency level.
         */
        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> = apply {
            this.concurrencyLevel = concurrencyLevel
        }

        /**
         * Sets expire duration after each creating, replacing or reading.
         */
        fun expireAfterAccess(expireAfterAccess: Duration): Builder<K, V> = apply {
            this.expireAfterAccess = expireAfterAccess
        }

        /**
         * Sets expire duration after each creating or replacing.
         */
        fun expireAfterWrite(expireAfterWrite: Duration): Builder<K, V> = apply {
            this.expireAfterWrite = expireAfterWrite
        }

        /**
         * Sets refresh duration after each creating or replacing,
         * the refresh action is specified by [loader].
         */
        fun refreshAfterWrite(refreshAfterWrite: Duration): Builder<K, V> = apply {
            this.refreshAfterWrite = refreshAfterWrite
        }

        /**
         * Sets loader used to automatically load the value if the entry is miss.
         * The loader should use [Val] to wrap loaded value, or -- if loading failed, return null.
         */
        fun loader(loader: Function<in K, out V>): Builder<K, V> = apply {
            this.loader = loader
        }

        /**
         * Let this builder use guava implementation [GuavaCache] to build the [Cache].
         * By default, this builder will use [CaffeineCache].
         */
        fun useGuava(useGuava: Boolean): Builder<K, V> = apply {
            this.useGuava = useGuava
        }

        /**
         * Builds the cache.
         */
        fun build(): Cache<K, V> {
            val loader = this.loader
            return if (useGuava) {
                if (loader === null) GuavaCache(this) else LoadingGuavaCache(this)
            } else {
                if (loader === null) CaffeineCache(this) else LoadingCaffeineCache(this)
            }
        }
    }

    companion object {

        /**
         * Returns new builder for Cache interface.
         */
        @JvmStatic
        fun <K : Any, V : Any> newBuilder(): Builder<K, V> {
            return Builder()
        }

        /**
         * Returns a Cache implemented by Map.
         *
         * Note whether the returned cache is thread-safe depends on given [this] map.
         */
        @JvmName("ofMap")
        @JvmOverloads
        @JvmStatic
        fun <K : Any, V : Any> MutableMap<K, CacheVal<V>>.asCache(
            loader: Function<in K, out V>? = null
        ): Cache<K, V> {
            return if (loader === null) MapCache(this) else LoadingMapCache(this, loader)
        }

        /**
         * Return a Cache implemented by `Weak Reference Map`.
         */
        @JvmOverloads
        @JvmStatic
        fun <K : Any, V : Any> ofWeak(
            concurrencyLevel: Int = defaultConcurrencyLevel(),
            loader: Function<in K, out V>? = null,
        ): Cache<K, V> {
            return MapMaker().concurrencyLevel(concurrencyLevel).weakValues().makeMap<K, CacheVal<V>>().asCache(loader)
        }
    }
}
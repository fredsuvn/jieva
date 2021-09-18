package xyz.srclab.common.base

/**
 * Cacheable builder, an abstract build which stores last build result from [buildNew].
 *
 * By default, if there is no modification, [build] method will return last result of [buildNew].
 * Subclass should call [commitModification] for each `set` operation, which leads the cache expired.
 */
abstract class CacheableBuilder<T : Any> {

    private var cache: T? = null
    private var version: Int = 0
    private var buildVersion: Int = 0

    /**
     * Returns a new result value and stores it as cache.
     */
    protected abstract fun buildNew(): T

    /**
     * Commits modification of this Builder, lead cache refresh.
     */
    protected open fun commitModification() {
        version++
        if (version == buildVersion) {
            version++
        }
    }

    /**
     * Returns last cache result if no modification, or new one if this build is modified.
     */
    open fun build(): T {
        val cache = this.cache
        if (cache === null || version != buildVersion) {
            val result = buildNew()
            this.cache = result
            buildVersion = version
            return result
        }
        return cache
    }
}
package xyz.srclab.common.lang

/**
 * Abstract Builder class to help cache last result value.
 * If there is no modification, [build] method will always return last result value.
 * Note [commitModification] should be called for each operation which may lead the cache expired
 * (such as setXxx method).
 */
abstract class CachingProductBuilder<T : Any> {

    private var cache: T? = null
    private var version: Int = 0
    private var buildVersion: Int = 0

    /**
     * Returns a new result value.
     */
    protected abstract fun buildNew(): T

    /**
     * Commits modification of this Builder, may lead cache refresh.
     */
    protected open fun commitModification() {
        version++
        if (version == buildVersion) {
            version++
        }
    }

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
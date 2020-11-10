package xyz.srclab.common.base

abstract class BaseCachingProductBuilder<T : Any> {

    private var cache: T? = null
    private var version: Int = 0
    private var buildVersion: Int = 0

    protected fun buildCaching(): T {
        if (cache === null || version != buildVersion) {
            cache = buildNew()
            buildVersion = version
        }
        return cache.asNotNull()
    }

    /**
     * Called after any change which leads to refresh cache.
     */
    protected fun commitChange() {
        version++
        if (version == buildVersion) {
            version++
        }
    }

    protected abstract fun buildNew(): T
}

abstract class CachingProductBuilder<T : Any> : BaseCachingProductBuilder<T>() {

    fun build(): T {
        return buildCaching()
    }
}
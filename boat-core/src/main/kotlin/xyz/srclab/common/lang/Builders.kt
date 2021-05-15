package xyz.srclab.common.lang

/**
 * Help cache last build value if no more modification.
 *
 * Synchronized version is [SyncCachingProductBuilder]
 *
 * @see SyncCachingProductBuilder
 */
abstract class CachingProductBuilder<T : Any> {

    private var cache: T? = null
    private var version: Int = 0
    private var buildVersion: Int = 0

    protected abstract fun buildNew(): T

    /**
     * Called after any change which leads to refresh cache.
     */
    protected open fun commitChange() {
        version++
        if (version == buildVersion) {
            version++
        }
    }

    open fun build(): T {
        if (cache === null || version != buildVersion) {
            cache = buildNew()
            buildVersion = version
        }
        return cache.asNotNull()
    }
}

/**
 * Help cache last build value if no more modification.
 *
 * This is synchronized version, non-synchronized version is [CachingProductBuilder]
 *
 * @see CachingProductBuilder
 */
abstract class SyncCachingProductBuilder<T : Any> : CachingProductBuilder<T>() {

    private var cache: T? = null
    private var version: Int = 0
    private var buildVersion: Int = 0

    /**
     * Called after any change which leads to refresh cache.
     */
    override fun commitChange() {
        synchronized(this) {
            super.commitChange()
        }
    }

    override fun build(): T {
        synchronized(this) {
            return super.build()
        }
    }
}
package xyz.srclab.common.cache

import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable

/**
 * Cache exception.
 */
open class CacheException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable {

    constructor(cause: Throwable?) : this(null, cause)

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}
package xyz.srclab.common.codec

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

/**
 * Codec exception.
 */
open class CodecException @JvmOverloads constructor(
    name: String, cause: Throwable? = null
) : RuntimeException(name, cause), Serializable {
    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
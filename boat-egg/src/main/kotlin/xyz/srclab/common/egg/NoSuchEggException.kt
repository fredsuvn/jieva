package xyz.srclab.common.egg

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

class NoSuchEggException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable {
    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
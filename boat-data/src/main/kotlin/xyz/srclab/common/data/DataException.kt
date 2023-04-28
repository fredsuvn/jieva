package xyz.srclab.common.data

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

/**
 * Data exception.
 */
open class DataException(name: String) : RuntimeException(name), Serializable {
    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
package xyz.srclab.common.bean

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

/**
 * Represents the property was not found.
 */
open class NoSuchPropertyException(name: String) : RuntimeException(name), Serializable {
    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
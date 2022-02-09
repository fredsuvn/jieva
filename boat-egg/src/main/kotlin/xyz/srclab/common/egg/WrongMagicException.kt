package xyz.srclab.common.egg

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

class WrongMagicException : Serializable, RuntimeException(
    "WRONG MAGIC!"
) {
    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
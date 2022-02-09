package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OConfig : Serializable {

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
        val EMPTY = OConfig()
    }
}
package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OOrder : Serializable {

    var player: Int = 0
    var order: Int = 0

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
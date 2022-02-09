package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OData : Serializable {

    var config: OConfig = OConfig.EMPTY
    var players: Array<OPlayer> = emptyArray()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
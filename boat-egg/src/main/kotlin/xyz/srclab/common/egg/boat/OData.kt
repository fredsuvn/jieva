package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OData : Serializable {

    lateinit var config: OConfig
    lateinit var p1: OPlayer// human down-left
    lateinit var p2: OPlayer// human down-right
    lateinit var p3: OPlayer// human up-left
    lateinit var p4: OPlayer// human up-right
    lateinit var botUp: OPlayer// bot up
    lateinit var botDown: OPlayer// bot down
    lateinit var tanks: MutableList<OTank>
    lateinit var bullets: MutableList<OBullet>

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}
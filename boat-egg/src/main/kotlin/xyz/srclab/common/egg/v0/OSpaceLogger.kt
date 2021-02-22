package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.egg.sample.Logger

/**
 * @author sunqian
 */
internal object OSpaceLogger : Logger {

    var info1: OSpaceInfo? = null
    var info2: OSpaceInfo? = null

    override fun info(pattern: String, vararg args: Any?) {
        //println("${Current.timestamp}-info: " + pattern.fastFormat(*args))
        synchronized(this) {
            this.info1 = this.info2
            this.info2 = OSpaceInfo(
                pattern.fastFormat(*args),
                Current.timestamp
            )
        }
    }

    override fun debug(pattern: String, vararg args: Any?) {
        //println("${Current.timestamp}-debug: " + pattern.fastFormat(*args))
    }
}

internal data class OSpaceInfo(
    var message: String,
    var timestamp: String,
)
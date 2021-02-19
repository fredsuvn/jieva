package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.egg.sample.Logger

/**
 * @author sunqian
 */
object OSpaceLogger : Logger {

    override fun info(pattern: String, vararg args: Any?) {
        println("${Current.timestamp}-info: " + pattern.fastFormat(*args))
    }

    override fun debug(pattern: String, vararg args: Any?) {
        println("${Current.timestamp}-debug: " + pattern.fastFormat(*args))
    }
}
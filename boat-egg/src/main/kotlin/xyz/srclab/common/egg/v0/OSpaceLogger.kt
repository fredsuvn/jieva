package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.egg.sample.Logger

/**
 * @author sunqian
 */
class OSpaceLogger : Logger {

    override fun info(pattern: String, vararg args: Any?) {
        println("info: " + pattern.fastFormat(*args))
    }

    override fun debug(pattern: String, vararg args: Any?) {
        println("debug: " + pattern.fastFormat(*args))
    }
}
package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Logger of [Egg].
 *
 * @author sunqian
 */
interface Logger {

    fun info(pattern: String, vararg args: Any?)

    fun debug(pattern: String, vararg args: Any?)
}
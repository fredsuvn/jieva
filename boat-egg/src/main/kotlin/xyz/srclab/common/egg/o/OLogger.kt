package xyz.srclab.common.egg.o

import xyz.srclab.common.lang.CharsFormat.Companion.fastFormat

object OLogger {

    const val debug = true
    //const val xyz.srclab.common.base.debug = false

    fun debug(pattern: String, vararg args: Any?) {
        if (!debug) {
            return
        }
        println(pattern.fastFormat(*args))
    }
}
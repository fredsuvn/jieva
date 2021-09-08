package xyz.srclab.common.base

import xyz.srclab.common.base.CharsFormat.Companion.fastFormat

private const val ENABLE_DEBUG = true

/**
 * Internal log for debug.
 */
interface Log {

    /**
     * Debug level.
     */
    fun debug(pattern: String, vararg args: Any?)

    /**
     * Info level.
     */
    fun info(pattern: String, vararg args: Any?)

    companion object {

        /**
         * Returns default [Log].
         */
        @JvmField
        val DEFAULT: Log = LogImpl

        private object LogImpl : Log {

            override fun debug(pattern: String, vararg args: Any?) {
                if (!ENABLE_DEBUG) {
                    return
                }
                log("DEBUG", pattern, *args)
            }

            override fun info(pattern: String, vararg args: Any?) {
                log("INFO", pattern, *args)
            }

            private fun log(level: String, pattern: String, vararg args: Any?) {
                val frame = callerStackTrace { e, findCalled ->
                    if (!findCalled && e.className == LogImpl::class.java.name && e.methodName == "log") {
                        return@callerStackTrace 0
                    }
                    if (findCalled && e.className != LogImpl::class.java.name) {
                        return@callerStackTrace 1
                    }
                    -1
                }
                val message = if (frame === null) {
                    ">>> [$level] ${pattern.fastFormat(*args)}"
                } else {
                    "${frame.className}.${frame.methodName}(${frame.lineNumber}) [$level] ${pattern.fastFormat(*args)}"
                }
                println(message)
            }
        }
    }
}
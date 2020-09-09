package xyz.srclab.common.base

import org.slf4j.helpers.MessageFormatter
import java.text.MessageFormat
import java.util.*

interface Format {

    fun format(pattern: String, vararg args: Any?): String

    companion object {

        @JvmStatic
        fun fastFormat(pattern: String, vararg args: Any?): String {
            return FastFormat.format(pattern, *args)
        }

        @JvmStatic
        fun printfFormat(pattern: String, vararg args: Any?): String {
            return String.format(Locale.getDefault(), pattern, *args)
        }

        @JvmStatic
        fun messageFormat(pattern: String, vararg args: Any?): String {
            return MessageFormat.format(pattern, *args)
        }

        private object FastFormat : Format {

            override fun format(pattern: String, vararg args: Any?): String {
                processArguments(As.notNull(args))
                return MessageFormatter.arrayFormat(pattern, args, null).message
            }

            private fun processArguments(args: Array<Any?>) {
                if (args.isEmpty()) {
                    return
                }
                val lastElement = args[args.size - 1]
                if (lastElement is Throwable) {
                    args[args.size - 1] = lastElement.toString()
                }
            }
        }
    }
}
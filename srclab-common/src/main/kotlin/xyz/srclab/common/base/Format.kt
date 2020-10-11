@file:JvmName("Format")
@file:JvmMultifileClass
package xyz.srclab.common.base

import java.text.MessageFormat
import org.slf4j.helpers.MessageFormatter as Slf4jMessageFormatter

interface Formatter {

    fun format(pattern: String, vararg args: Any?): String
}

fun fastFormat(pattern: String, vararg args: Any?): String {
    return FastFormatter.format(pattern, *args)
}

fun printfFormat(pattern: String, vararg args: Any?): String {
    return PrintfFormatter.format(pattern, *args)
}

fun messageFormat(pattern: String, vararg args: Any?): String {
    return MessageFormatter.format(pattern, *args)
}

object FastFormatter : Formatter {

    override fun format(pattern: String, vararg args: Any?): String {
        processArguments(args.asAny())
        return Slf4jMessageFormatter.arrayFormat(pattern, args, null).message
    }

    private fun processArguments(args: Array<Any?>) {
        if (args.isEmpty()) {
            return
        }

        /*
        val lastElement = args[args.size - 1]
        if (lastElement is Throwable) {
            args[args.size - 1] = lastElement.toString()
        }
         */
    }
}

object PrintfFormatter : Formatter {

    override fun format(pattern: String, vararg args: Any?): String {
        return String.format(Defaults.locale, pattern, *args)
    }
}

object MessageFormatter : Formatter {

    override fun format(pattern: String, vararg args: Any?): String {
        return MessageFormat.format(pattern, *args)
    }
}
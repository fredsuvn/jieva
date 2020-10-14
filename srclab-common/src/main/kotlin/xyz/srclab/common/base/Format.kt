package xyz.srclab.common.base

import org.slf4j.helpers.MessageFormatter as Slf4jMessageFormatter
import java.text.MessageFormat as JavaMessageFormat

interface Format {

    fun format(pattern: String, vararg args: Any?): String

    companion object {

        @JvmStatic
        fun fastFormat(pattern: String, vararg args: Any?): String {
            return FastFormat.format(pattern, *args)
        }

        @JvmStatic
        fun printfFormat(pattern: String, vararg args: Any?): String {
            return PrintfFormat.format(pattern, *args)
        }

        @JvmStatic
        fun messageFormat(pattern: String, vararg args: Any?): String {
            return MessageFormat.format(pattern, *args)
        }
    }
}

fun String.fastFormat(vararg args: Any?): String {
    return Format.fastFormat(this, *args)
}

fun String.printfFormat(vararg args: Any?): String {
    return Format.printfFormat(this, *args)
}

fun String.messageFormat(vararg args: Any?): String {
    return Format.messageFormat(this, *args)
}

object FastFormat : Format {

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

object PrintfFormat : Format {

    override fun format(pattern: String, vararg args: Any?): String {
        return String.format(Defaults.locale, pattern, *args)
    }
}

object MessageFormat : Format {

    override fun format(pattern: String, vararg args: Any?): String {
        return JavaMessageFormat.format(pattern, *args)
    }
}
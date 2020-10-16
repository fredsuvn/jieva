package xyz.srclab.common.base

import org.slf4j.helpers.MessageFormatter as Slf4jMessageFormatter
import java.text.MessageFormat as JavaMessageFormat

interface Format {

    fun format(pattern: CharSequence, vararg args: Any?): String

    companion object {

        @JvmField
        val FAST_FORMAT: Format = FastFormat

        @JvmField
        val PRINTF_FORMAT: Format = PrintfFormat

        @JvmField
        val MESSAGE_FORMAT: Format = MessageFormat

        @JvmStatic
        fun fastFormat(pattern: CharSequence, vararg args: Any?): String {
            return FastFormat.format(pattern, *args)
        }

        @JvmStatic
        fun printfFormat(pattern: CharSequence, vararg args: Any?): String {
            return PrintfFormat.format(pattern, *args)
        }

        @JvmStatic
        fun messageFormat(pattern: CharSequence, vararg args: Any?): String {
            return MessageFormat.format(pattern, *args)
        }
    }
}

fun CharSequence.fastFormat(vararg args: Any?): String {
    return Format.fastFormat(this, *args)
}

fun CharSequence.printfFormat(vararg args: Any?): String {
    return Format.printfFormat(this, *args)
}

fun CharSequence.messageFormat(vararg args: Any?): String {
    return Format.messageFormat(this, *args)
}

object FastFormat : Format {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        processArguments(args.asAny())
        return Slf4jMessageFormatter.arrayFormat(pattern.toString(), args, null).message
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

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return String.format(Defaults.locale, pattern.toString(), *args)
    }
}

object MessageFormat : Format {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return JavaMessageFormat.format(pattern.toString(), *args)
    }
}
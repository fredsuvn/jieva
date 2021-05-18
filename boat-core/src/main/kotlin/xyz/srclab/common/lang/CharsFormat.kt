package xyz.srclab.common.lang

import org.slf4j.helpers.MessageFormatter as MessageFormatterSlf4j
import java.text.MessageFormat as MessageFormatKt

/**
 * Chars format.
 *
 * @see FastCharsFormat
 * @see PrintfCharsFormat
 * @see MessageCharsFormat
 */
interface CharsFormat {

    fun format(pattern: CharSequence, vararg args: Any?): String

    companion object {

        @JvmField
        val FAST_FORMAT: FastCharsFormat = FastCharsFormat

        @JvmField
        val PRINT_FORMAT: PrintfCharsFormat = PrintfCharsFormat

        @JvmField
        val MESSAGE_FORMAT: MessageCharsFormat = MessageCharsFormat

        @JvmStatic
        fun CharSequence.fastFormat(vararg args: Any?): String {
            return FastCharsFormat.format(this, *args)
        }

        @JvmStatic
        fun CharSequence.printfFormat(vararg args: Any?): String {
            return PrintfCharsFormat.format(this, *args)
        }

        @JvmStatic
        fun CharSequence.messageFormat(vararg args: Any?): String {
            return MessageCharsFormat.format(this, *args)
        }
    }
}

/**
 * [CharsFormat] with `slf4j` style.
 */
object FastCharsFormat : CharsFormat {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        processArguments(args)
        return MessageFormatterSlf4j.arrayFormat(pattern.toString(), args, null).message
    }

    private fun processArguments(array: Array<out Any?>) {
        if (array.isEmpty()) {
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

/**
 * [CharsFormat] with `System.out.printf` style.
 */
object PrintfCharsFormat : CharsFormat {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return String.format(Defaults.locale, pattern.toString(), *args)
    }
}

/**
 * [CharsFormat] with `String.format` style.
 */
object MessageCharsFormat : CharsFormat {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return MessageFormatKt.format(pattern.toString(), *args)
    }
}
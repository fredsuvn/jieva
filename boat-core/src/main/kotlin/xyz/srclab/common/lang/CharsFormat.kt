package xyz.srclab.common.lang

import org.slf4j.helpers.MessageFormatter as MessageFormatterSlf4j
import java.text.MessageFormat as MessageFormatKt

/**
 * Format for [CharSequence].
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
        val PRINTF_FORMAT: PrintfCharsFormat = PrintfCharsFormat

        @JvmField
        val MESSAGE_FORMAT: MessageCharsFormat = MessageCharsFormat

        /**
         * Formats by [FastCharsFormat].
         */
        @JvmStatic
        fun CharSequence.fastFormat(vararg args: Any?): String {
            return FAST_FORMAT.format(this, *args)
        }

        /**
         * Formats by [PrintfCharsFormat].
         */
        @JvmStatic
        fun CharSequence.printfFormat(vararg args: Any?): String {
            return PRINTF_FORMAT.format(this, *args)
        }

        /**
         * Formats by [MessageCharsFormat].
         */
        @JvmStatic
        fun CharSequence.messageFormat(vararg args: Any?): String {
            return MESSAGE_FORMAT.format(this, *args)
        }
    }
}

/**
 * [CharsFormat] with `slf4j` style.
 */
object FastCharsFormat : CharsFormat {
    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return MessageFormatterSlf4j.arrayFormat(pattern.toString(), args, null).message
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
 * [CharsFormat] with `MessageFormat` style.
 */
object MessageCharsFormat : CharsFormat {
    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return MessageFormatKt.format(pattern.toString(), *args)
    }
}
@file:JvmName("BFormats")

package xyz.srclab.common.base

import org.slf4j.helpers.MessageFormatter as MessageFormatterSlf4j
import java.text.MessageFormat as MessageFormatKt

/**
 * Formats by [FastCharsFormat].
 */
fun CharSequence.fastFormat(vararg args: Any?): String {
    return BFormat.FAST_FORMAT.format(this, *args)
}

/**
 * Formats by [PrintfCharsFormat].
 */
fun CharSequence.printfFormat(vararg args: Any?): String {
    return BFormat.PRINTF_FORMAT.format(this, *args)
}

/**
 * Formats by [MessageCharsFormat].
 */
fun CharSequence.messageFormat(vararg args: Any?): String {
    return BFormat.MESSAGE_FORMAT.format(this, *args)
}

/**
 * Format for [CharSequence].
 *
 * @see FastCharsFormat
 * @see PrintfCharsFormat
 * @see MessageCharsFormat
 */
interface BFormat {

    fun format(pattern: CharSequence, vararg args: Any?): String

    companion object {

        @JvmField
        val FAST_FORMAT: FastCharsFormat = FastCharsFormat

        @JvmField
        val PRINTF_FORMAT: PrintfCharsFormat = PrintfCharsFormat

        @JvmField
        val MESSAGE_FORMAT: MessageCharsFormat = MessageCharsFormat
    }
}

/**
 * [BFormat] with `slf4j` style.
 */
object FastCharsFormat : BFormat {
    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return MessageFormatterSlf4j.arrayFormat(pattern.toString(), args, null).message
    }
}

/**
 * [BFormat] with `System.out.printf` style.
 */
object PrintfCharsFormat : BFormat {
    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return String.format(pattern.toString(), *args)
    }
}

/**
 * [BFormat] with `MessageFormat` style.
 */
object MessageCharsFormat : BFormat {
    override fun format(pattern: CharSequence, vararg args: Any?): String {
        return MessageFormatKt.format(pattern.toString(), *args)
    }
}
@file:JvmName("BFormat")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import java.util.*

/**
 * Using [FastFormat] (slf4j-like style, but not exactly same) format receiver pattern,
 * seeing [FastFormat].
 *
 * @see FastFormat
 * @see CharsFormat
 */
fun CharSequence.fastFormat(vararg args: Any?): String {
    return FastFormat.format(this, *args)
}

/**
 * Used to format [CharSequence].
 *
 * @see FastFormat
 */
@ThreadSafe
interface CharsFormat {

    /**
     * Formats given [pattern] with [args].
     */
    fun format(pattern: CharSequence, vararg args: Any?): String
}

/**
 * Fast format using slf4j-like style (may not exactly same):
 *
 * ```
 * format("1 + 1 = {}, 2 + 2 = {}", 2, 4);
 * ```
 *
 * Using `\` as escape in following cases:
 *
 * * `\{}` will be escaped as `{}`;
 * * `\\` will be escaped as `\`;
 * * Otherwise no escape;
 *
 * For example:
 *
 * ```
 * //Note java string `\\` means `\`
 * format("C:\\\\{}\\_\\{}", "file.zip");
 * ```
 *
 * will output:
 *
 * ```
 * C:\file.zip\_{}
 * ```
 *
 * If Args' size less than `{}`, will output `{}` itself:
 *
 * ```
 * format("C:\\\\{}\\_{}", "file.zip");
 * ```
 *
 * will output:
 *
 * ```
 * C:\file.zip\_{}
 * ```
 */
object FastFormat : CharsFormat {

    override fun format(pattern: CharSequence, vararg args: Any?): String {
        //return MessageFormatterSlf4j.arrayFormat(this.toString(), args, null).message

        if (pattern.isEmpty()) {
            return pattern.toString()
        }

        var buffer: MutableList<Any?>? = null
        fun getBuffer(): MutableList<Any?> {
            val bf = buffer
            if (bf === null) {
                val newBuffer = LinkedList<Any?>()
                buffer = newBuffer
                return newBuffer
            }
            return bf
        }

        var start = 0
        var i = 0
        var argIndex = 0
        while (i < pattern.length) {
            val c = pattern[i]
            if (c == '\\') {
                i++
                if (i >= pattern.length) {
                    break
                }
                val cn = pattern[i]
                if (cn == '\\') {
                    //Escape: \\ -> \
                    getBuffer().add(pattern.charsRef(start, i))
                    i++
                    start = i
                    continue
                }
                if (cn == '{') {
                    i++
                    if (i > pattern.length) {
                        break
                    }
                    val cnn = pattern[i]
                    if (cnn == '}') {
                        //Escape: \{} -> {}
                        getBuffer().add(pattern.charsRef(start, i - 2))
                        start = i - 1
                        i++
                        continue
                    }
                }
            }
            if (c == '{') {
                i++
                if (i >= pattern.length) {
                    break
                }
                val cn = pattern[i]
                if (cn == '}') {
                    //Insert parameter
                    if (!argIndex.isInBounds(0, args.size)) {
                        //Args bounds out of bounds
                        //throw StringFormatException("Argument index out of bounds: $argIndex")
                        i++
                        continue
                    }
                    getBuffer().add(pattern.charsRef(start, i - 1))
                    getBuffer().add(args[argIndex])
                    i++
                    start = i
                    argIndex++
                    continue
                }
            }
            i++
        }

        if (buffer === null) {
            return pattern.toString()
        }
        if (start < pattern.length) {
            getBuffer().add(pattern.charsRef(start))
        }

        return getBuffer().joinToString("") { it.deepToString() }
    }
}
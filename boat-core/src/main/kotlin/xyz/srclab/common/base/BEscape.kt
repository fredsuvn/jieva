@file:JvmName("BEscape")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*

/**
 * Simply escapes receiver [CharSequence] with [escapeChar].
 * It inserts [escapeChar] before each [escapedChars] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * BEscapes.escape("{\"ss\": \"sss\\n\"}", '\\', "\"{}");
 * ```
 */
@Throws(StringEscapeException::class)
fun CharSequence.escape(escapeChar: Char, escapedChars: CharSequence): String {
    if (this.isEmpty()) {
        return this.toString()
    }

    var buffer: MutableList<Any>? = null

    fun getBuffer(): MutableList<Any> {
        val bf = buffer
        if (bf === null) {
            val newBuffer = LinkedList<Any>()
            buffer = newBuffer
            return newBuffer
        }
        return bf
    }

    var start = 0
    var i = 0
    while (i < this.length) {
        val c = this[i]
        if (c == escapeChar || escapedChars.contains(c)) {
            //Escape: \ -> \\
            getBuffer().add(this.stringRef(start, i))
            getBuffer().add(escapeChar)
            start = i
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.stringRef(start))
    }

    return getBuffer().joinToString("")
}

/**
 * Simply unescapes receiver [CharSequence] with [escapeChar].
 * It removes [escapeChar] before each [escapedChars] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * BEscapes.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}", '\\', "\"{}");
 * ```
 */
@Throws(StringEscapeException::class)
fun CharSequence.unescape(escapeChar: Char, escapedChars: CharSequence): String {
    if (this.isEmpty()) {
        return this.toString()
    }

    var buffer: MutableList<CharSequence>? = null

    fun getBuffer(): MutableList<CharSequence> {
        val bf = buffer
        if (bf === null) {
            val newBuffer = LinkedList<CharSequence>()
            buffer = newBuffer
            return newBuffer
        }
        return bf
    }

    var start = 0
    var i = 0
    while (i < this.length) {
        val c = this[i]
        if (c == escapeChar) {
            i++
            if (i >= this.length) {
                break
            }
            val cn = this[i]
            if (cn == escapeChar || escapedChars.contains(cn)) {
                //Unescape: \\ -> \
                getBuffer().add(this.stringRef(start, i - 1))
                start = i
            }
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.stringRef(start))
    }

    return getBuffer().joinToString("")
}

/**
 * Used to escape and unescape (may not be implemented) string.
 *
 * @see SimpleEscape
 */
@ThreadSafe
interface StringEscape {

    @Throws(StringEscapeException::class)
    fun escape(chars: CharSequence): String

    @Throws(StringEscapeException::class)
    fun unescape(chars: CharSequence): String
}

/**
 * A simple [StringEscape] implementation.
 * It supports define an escape char and a set of escaped chars,
 * simply inserts escape char before each escaped char when escaping, and removes when unescaping.
 * For example:
 *
 * ```
 * StringEscape se = new SimpleStringEscape('\\', "\"{}");
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * se.escape("{\"ss\": \"sss\\n\"}");
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * se.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}");
 * ```
 */
open class SimpleEscape(
    private val escapeChar: Char,
    private val escapedChars: CharSequence
) : StringEscape {

    override fun escape(chars: CharSequence): String {
        return chars.escape(escapeChar, escapedChars)
    }

    override fun unescape(chars: CharSequence): String {
        return chars.unescape(escapeChar, escapedChars)
    }
}

open class StringEscapeException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause)
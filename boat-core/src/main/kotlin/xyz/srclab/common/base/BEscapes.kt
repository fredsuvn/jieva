@file:JvmName("BEscapes")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*

/**
 * Simply escapes receiver [CharSequence] with [escapeChar].
 * It inserts [escapeChar] before each [escapedChars] when escaping:
 *
 * ```
 * //"ss": "sss\n" -> \"ss\": \"sss\\n\"
 * BEscapes.escape("\"ss\": \"sss\\n\"", '\\', '"', '{', '}');
 * ```
 */
fun CharSequence.escape(escapeChar: Char, vararg escapedChars: Char): String {
    return escape(escapeChar, escapedChars.toSet())
}

/**
 * Simply escapes receiver [CharSequence] with [escapeChar].
 * It inserts [escapeChar] before each [escapedChars] when escaping:
 *
 * ```
 * //"ss": "sss\n" -> \"ss\": \"sss\\n\"
 * BEscapes.escape("\"ss\": \"sss\\n\"", '\\', BSets.newSet('"', '{', '}'));
 * ```
 */
fun CharSequence.escape(escapeChar: Char, escapedChars: Collection<Char>): String {

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
 * //\"ss\": \"sss\\n\" -> "ss": "sss\n"
 * BEscapes.unescape("\\\"ss\\\": \\\"sss\\\\n\\\"", '\\', '"', '{', '}');
 * ```
 */
fun CharSequence.unescape(escapeChar: Char, vararg escapedChars: Char): String {
    return unescape(escapeChar, escapedChars.toSet())
}

/**
 * Simply unescapes receiver [CharSequence] with [escapeChar].
 * It removes [escapeChar] before each [escapedChars] when unescaping:
 *
 * ```
 * //\"ss\": \"sss\\n\" -> "ss": "sss\n"
 * BEscapes.unescape("\\\"ss\\\": \\\"sss\\\\n\\\"", '\\', BSets.newSet('"', '{', '}'));
 * ```
 */
fun CharSequence.unescape(escapeChar: Char, escapedChars: Collection<Char>): String {

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
            if (i < this.length) {
                val cn = this[i]
                if (escapedChars.contains(cn)) {
                    //Unescape: \\ -> \
                    getBuffer().add(this.stringRef(start, i - 1))
                    start = i
                }
            } else {
                break
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
 * @see SimpleStringEscape
 */
@ThreadSafe
interface StringEscape {

    fun escape(chars: CharSequence): String

    fun unescape(chars: CharSequence): String
}

/**
 * A simple [StringEscape] implementation.
 * It supports define an escape char and a set of escaped chars,
 * simply inserts escape char before each escaped char when escaping, and removes when unescaping.
 * For example:
 *
 * ```
 * StringEscape se = new SimpleStringEscape('\\', '"', '{', '}');
 * //"ss": "sss\n" -> \"ss\": \"sss\\n\"
 * se.escape("\"ss\": \"sss\\n\"");
 * //\"ss\": \"sss\\n\" -> "ss": "sss\n"
 * se.unescape("\\\"ss\\\": \\\"sss\\\\n\\\"");
 * ```
 */
open class SimpleStringEscape(
    private val escapeChar: Char,
    private val escapedChars: Collection<Char>
) : StringEscape {

    constructor(escapeChar: Char, vararg escapedChars: Char) : this(escapeChar, escapedChars.toSet())

    override fun escape(chars: CharSequence): String {
        return chars.escape(escapeChar, escapedChars)
    }

    override fun unescape(chars: CharSequence): String {
        return chars.unescape(escapeChar, escapedChars)
    }
}
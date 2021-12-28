@file:JvmName("BEscapes")

package xyz.srclab.common.base

import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*

/**
 * Escapes receiver [CharSequence] with [escapeChar]:
 *
 * ```
 * //"ss": "sss\n" -> \"ss\": \"sss\\n\"
 * BEscapes.escape("\"ss\": \"sss\\n"", '\\', '"', '{', '}');
 * ```
 */
fun CharSequence.escape(escapeChar: Char, vararg escapedChars: Char): String {
    return escape(escapeChar, escapedChars.toSet())
}

/**
 * Escapes receiver [CharSequence] with [escapeChar]:
 *
 * ```
 * //"ss": "sss\n" -> \"ss\": \"sss\\n\"
 * BEscapes.escape("\"ss\": \"sss\\n"", '\\', BSets.newSet('"', '{', '}'));
 * ```
 */
fun CharSequence.escape(escapeChar: Char, escapedChars: Collection<Char>): String {

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
 * Unescapes receiver [CharSequence] with [escapeChar]:
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
 * Unescapes receiver [CharSequence] with [escapeChar]:
 *
 * ```
 * //\"ss\": \"sss\\n\" -> "ss": "sss\n"
 * BEscapes.unescape("\\\"ss\\\": \\\"sss\\\\n\\\"", '\\', BSets.newSet('"', '{', '}'));
 * ```
 */
fun CharSequence.unescape(escapeChar: Char, escapedChars: Collection<Char>): String {

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
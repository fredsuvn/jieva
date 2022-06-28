@file:JvmName("BEscape")

package xyz.srclab.common.base

import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.util.*

/**
 * Simply escapes receiver [InputStream] with [escapeByte].
 * It inserts [escapeByte] before each [escapedBytes] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * BEscapes.escape("{\"ss\": \"sss\\n\"}".asInputStream(), '\\'.toByte(), "\"{}".toByteArray());
 * ```
 */
fun InputStream.escape(escapeByte: Byte, escapedBytes: ByteArray, dest: OutputStream): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toByte()
        if (bb == escapeByte || escapedBytes.contains(bb)) {
            //Escape: \ -> \\
            dest.write(escapeByte.toInt())
            dest.write(b)
            count += 2
        } else {
            dest.write(b)
            count++
        }
        b = this.read()
    }
    return count
}

/**
 * Simply escapes receiver [Reader] with [escapeChar].
 * It inserts [escapeChar] before each [escapedChars] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * BEscapes.escape("{\"ss\": \"sss\\n\"}".asReader(), '\\'.toByte(), "\"{}");
 * ```
 */
fun Reader.escape(escapeChar: Char, escapedChars: CharSequence, dest: Appendable): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toChar()
        if (bb == escapeChar || escapedChars.contains(bb)) {
            //Escape: \ -> \\
            dest.append(escapeChar)
            dest.append(bb)
            count += 2
        } else {
            dest.append(bb)
            count++
        }
        b = this.read()
    }
    return count
}

/**
 * Simply escapes receiver [CharSequence] with [escapeChar].
 * It inserts [escapeChar] before each [escapedChars] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * BEscapes.escape("{\"ss\": \"sss\\n\"}", '\\', "\"{}");
 * ```
 */
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
            getBuffer().add(this.charsRef(start, i))
            getBuffer().add(escapeChar)
            start = i
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.charsRef(start))
    }

    return getBuffer().joinToString("")
}

/**
 * Simply unescapes receiver [InputStream] with [escapeByte].
 * It removes [escapeByte] before each [escapedBytes] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * BEscapes.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}".asInputStream(), '\\'.toByte(), "\"{}".toByteArray());
 * ```
 */
fun InputStream.unescape(escapeByte: Byte, escapedBytes: ByteArray, dest: OutputStream): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toByte()
        if (bb == escapeByte) {
            //Escape: \\ -> \
            val bn = this.read()
            if (bn == -1) {
                dest.write(b)
                count++
                break
            }
            val bnb = bn.toByte()
            if (bnb == escapeByte || escapedBytes.contains(bnb)) {
                dest.write(bn)
                count++
            } else {
                dest.write(b)
                dest.write(bn)
                count += 2
            }
        } else {
            dest.write(b)
            count++
        }
        b = this.read()
    }
    return count
}

/**
 * Simply unescapes receiver [Reader] with [escapeChar].
 * It removes [escapeChar] before each [escapedChars] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * BEscapes.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}".asReader(), '\\', "\"{}");
 * ```
 */
fun Reader.unescape(escapeChar: Char, escapedChars: CharSequence, dest: Appendable): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toChar()
        if (bb == escapeChar) {
            //Escape: \\ -> \
            val bn = this.read()
            if (bn == -1) {
                dest.append(bb)
                count++
                break
            }
            val bnb = bn.toChar()
            if (bnb == escapeChar || escapedChars.contains(bnb)) {
                dest.append(bnb)
                count++
            } else {
                dest.append(bb)
                dest.append(bnb)
                count += 2
            }
        } else {
            dest.append(bb)
            count++
        }
        b = this.read()
    }
    return count
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
                getBuffer().add(this.charsRef(start, i - 1))
                start = i
            }
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.charsRef(start))
    }

    return getBuffer().joinToString("")
}
/**
 * Escape utilities.
 */
@file:JvmName("EscapeBt")

package xyz.srclab.common.base

import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.util.*

/**
 * Simply escapes receiver [InputStream] with [escByte].
 * It inserts [escByte] before each byte of [needBytes] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * EscapeBt.escape("{\"ss\": \"sss\\n\"}".asInputStream(), '\\'.toByte(), "\"{}".toByteArray());
 * ```
 */
fun InputStream.escape(escByte: Byte, needBytes: ByteArray, dest: OutputStream): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toByte()
        if (bb == escByte || needBytes.contains(bb)) {
            //Escape: \ -> \\
            dest.write(escByte.toInt())
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
 * Simply escapes receiver [Reader] with [escChar].
 * It inserts [escChar] before each char of [needChars] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * EscapeBt.escape("{\"ss\": \"sss\\n\"}".asReader(), '\\'.toByte(), "\"{}");
 * ```
 */
fun Reader.escape(escChar: Char, needChars: CharSequence, dest: Appendable): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toChar()
        if (bb == escChar || needChars.contains(bb)) {
            //Escape: \ -> \\
            dest.append(escChar)
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
 * Simply escapes receiver [CharSequence] with [escChar].
 * It inserts [escChar] before each char of [needChars] when escaping:
 *
 * ```
 * //{"ss": "sss\n"} -> \{\"ss\": \"sss\\n\"\}
 * EscapeBt.escape("{\"ss\": \"sss\\n\"}", '\\', "\"{}");
 * ```
 */
fun CharSequence.escape(escChar: Char, needChars: CharSequence): String {
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
        if (c == escChar || needChars.contains(c)) {
            //Escape: \ -> \\
            getBuffer().add(this.subRef(start, i))
            getBuffer().add(escChar)
            start = i
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.subRef(start))
    }

    return getBuffer().joinToString("")
}

/**
 * Simply unescapes receiver [InputStream] with [escByte].
 * It removes [escByte] before each byte of [needBytes] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * EscapeBt.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}".asInputStream(), '\\'.toByte(), "\"{}".toByteArray());
 * ```
 */
fun InputStream.unescape(escByte: Byte, needBytes: ByteArray, dest: OutputStream): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toByte()
        if (bb == escByte) {
            //Escape: \\ -> \
            val bn = this.read()
            if (bn == -1) {
                dest.write(b)
                count++
                break
            }
            val bnb = bn.toByte()
            if (bnb == escByte || needBytes.contains(bnb)) {
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
 * Simply unescapes receiver [Reader] with [escChar].
 * It removes [escChar] before each char of [needChars] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * EscapeBt.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}".asReader(), '\\', "\"{}");
 * ```
 */
fun Reader.unescape(escChar: Char, needChars: CharSequence, dest: Appendable): Long {
    var count = 0L
    var b = this.read()
    while (b != -1) {
        val bb = b.toChar()
        if (bb == escChar) {
            //Escape: \\ -> \
            val bn = this.read()
            if (bn == -1) {
                dest.append(bb)
                count++
                break
            }
            val bnb = bn.toChar()
            if (bnb == escChar || needChars.contains(bnb)) {
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
 * Simply unescapes receiver [CharSequence] with [escChar].
 * It removes [escChar] before each char of [needChars] when unescaping:
 *
 * ```
 * //\{\"ss\": \"sss\\n\"\} -> {"ss": "sss\n"}
 * EscapeBt.unescape("\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}", '\\', "\"{}");
 * ```
 */
fun CharSequence.unescape(escChar: Char, needChars: CharSequence): String {
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
        if (c == escChar) {
            i++
            if (i >= this.length) {
                break
            }
            val cn = this[i]
            if (cn == escChar || needChars.contains(cn)) {
                //Unescape: \\ -> \
                getBuffer().add(this.subRef(start, i - 1))
                start = i
            }
        }
        i++
    }

    if (buffer === null) {
        return this.toString()
    }
    if (start < this.length) {
        getBuffer().add(this.subRef(start))
    }

    return getBuffer().joinToString("")
}
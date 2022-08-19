package xyz.srclab.common.base

import xyz.srclab.common.asType
import xyz.srclab.common.collect.arrayOfType
import xyz.srclab.common.remLength
import java.io.OutputStream
import java.io.Serializable
import java.io.Writer
import java.nio.ByteBuffer
import java.util.function.Supplier

private const val BUFFER_SIZE = 1024

/**
 * Chars builder, used to append given objects and concatenate them to [String] or [CharArray].
 *
 * This class is lazy, it only stores given appended object, without any other computation.
 * It concatenates appended objects when the [toString] or [toCharArray] method is called,
 * so any modification (before or in processing of [toString] or [toCharArray]) for the content of appended object
 * will affect the result.
 */
open class CharsBuilder : Appendable, Writer(), Serializable {

    private var head = SNode<Any>()
    private var cur = head

    private var buffer: CharArray? = null
    private var bufferOffset = 0

    override fun append(t: CharSequence?): CharsBuilder {
        flushBuffer()
        cur.value = t
        newTail()
        return this
    }

    fun append(t: CharSequence, startIndex: Int): CharsBuilder {
        flushBuffer()
        cur.value = t.charsRef(startIndex)
        newTail()
        return this
    }

    override fun append(t: CharSequence, startIndex: Int, endIndex: Int): CharsBuilder {
        flushBuffer()
        cur.value = t.charsRef(startIndex, endIndex)
        newTail()
        return this
    }

    override fun append(c: Char): CharsBuilder {
        val buf = buffer
        if (buf === null) {
            val newBuffer = CharArray(BUFFER_SIZE)
            newBuffer[0] = c
            bufferOffset = 1
            buffer = newBuffer
            return this
        }
        buf[bufferOffset] = c
        bufferOffset++
        if (bufferOffset >= buf.size) {
            cur.value = buf.copyOf()
            bufferOffset = 0
            newTail()
        }
        return this
    }

    open fun append(obj: Any?): CharsBuilder {
        flushBuffer()
        cur.value = obj
        newTail()
        return this
    }

    open fun append(chars: CharArray): CharsBuilder {
        flushBuffer()
        cur.value = chars
        newTail()
        return this
    }

    open fun append(chars: CharArray, startIndex: Int): CharsBuilder {
        flushBuffer()
        cur.value = chars.charsRef(startIndex)
        newTail()
        return this
    }

    open fun append(chars: CharArray, startIndex: Int, endIndex: Int): CharsBuilder {
        flushBuffer()
        cur.value = chars.charsRef(startIndex, endIndex)
        newTail()
        return this
    }

    fun isEmpty(): Boolean {
        return head.next === null
    }

    override fun write(c: Int) {
        append(c.toChar())
    }

    override fun write(cbuf: CharArray) {
        append(cbuf)
    }

    override fun write(str: String) {
        append(str)
    }

    override fun write(str: String, off: Int, len: Int) {
        append(str, off, len)
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        append(cbuf, off, len)
    }

    /**
     * Clears content of this appender.
     */
    open fun clear() {
        val newNode = SNode<Any>()
        head = newNode
        cur = newNode
    }

    /**
     * Concatenates appended objects to [String].
     */
    override fun toString(): String {
        return String(toCharArray())
    }

    /**
     * Concatenates appended objects to [CharArray].
     */
    fun toCharArray(): CharArray {
        // Computes size:
        var size = 0
        var n = head
        while (n.next !== null) {
            when (val v = n.value) {
                null -> size += BtProps.nullString().length
                is CharSequence -> size += v.length
                is CharArray -> size += v.size
                is Char -> size += 1
                else -> {
                    val s = v.toString()
                    n.value = s
                    size += s.length
                }
            }
            n = n.next!!
        }
        // Fills buffer
        val buffer = CharArray(size)
        var i = 0
        n = head
        while (n.next !== null) {
            val s: Int = when (val v = n.value) {
                null -> {
                    BtProps.nullString().asJavaString().getChars(0, BtProps.nullString().length, buffer, i)
                    BtProps.nullString().length
                }
                is String -> {
                    v.asJavaString().getChars(0, v.length, buffer, i)
                    v.length
                }
                is CharsRef -> {
                    v.copyTo(buffer, i)
                    v.length
                }
                is CharSequence -> {
                    v.getChars(buffer, i, v.length)
                    v.length
                }
                is CharArray -> {
                    System.arraycopy(v, 0, buffer, i, v.size)
                    v.size
                }
                is Char -> {
                    buffer[i] = v
                    1
                }
                else -> throw IllegalStateException("Unknown node value type: ${v.javaClass}")
            }
            i += s
            n = n.next!!
        }
        return buffer
    }

    override fun close() {
    }

    override fun flush() {
    }

    private fun flushBuffer() {
        val buf = buffer
        if (buf === null || bufferOffset == 0) {
            return
        }
        cur.value = buf.copyOfRange(0, bufferOffset)
        bufferOffset = 0
        newTail()
    }

    private fun newTail() {
        val newNode = SNode<Any>()
        cur.next = newNode
        cur = newNode
    }

    companion object {
        /**
         * Supplier to create new [CharsBuilder].
         */
        @JvmField
        val SUPPLIER: Supplier<CharsBuilder> = Supplier { CharsBuilder() }
    }
}

/**
 * Bytes builder, used to append given objects and concatenate them to [ByteArray].
 *
 * This class is lazy, it only stores given appended object, without any other computation.
 * It concatenates appended objects when the [ByteArray] method is called,
 * so any modification (before or in processing of [ByteArray]) for the content of appended object
 * will affect the result.
 */
open class BytesBuilder : OutputStream(), Serializable {

    private var head = SNode<Any>()
    private var cur = head

    private var buffer: ByteArray? = null
    private var bufferOffset = 0

    /**
     * No operation. Appending null is unsupported by [BytesBuilder].
     */
    fun append(t: ByteArray?): BytesBuilder {
        if (t === null) {
            return this
        }
        flushBuffer()
        cur.value = t
        newTail()
        return this
    }

    fun append(t: ByteArray, startIndex: Int): BytesBuilder {
        flushBuffer()
        cur.value = ByteBuffer.wrap(t, startIndex, remLength(t.size, startIndex))
        newTail()
        return this
    }

    fun append(t: ByteArray, startIndex: Int, endIndex: Int): BytesBuilder {
        flushBuffer()
        cur.value = ByteBuffer.wrap(t, startIndex, endIndex)
        newTail()
        return this
    }

    open fun append(b: Byte): BytesBuilder {
        val buf = buffer
        if (buf === null) {
            val newBuffer = ByteArray(BUFFER_SIZE)
            newBuffer[0] = b
            bufferOffset = 1
            buffer = newBuffer
            return this
        }
        buf[bufferOffset] = b
        bufferOffset++
        if (bufferOffset >= buf.size) {
            cur.value = buf.copyOf()
            bufferOffset = 0
            newTail()
        }
        return this
    }

    open fun append(buf: ByteBuffer): BytesBuilder {
        flushBuffer()
        cur.value = buf
        newTail()
        return this
    }

    fun isEmpty(): Boolean {
        return head.next === null
    }

    override fun write(b: Int) {
        append(b.toByte())
    }

    override fun write(buf: ByteArray) {
        append(buf)
    }

    override fun write(buf: ByteArray, off: Int, len: Int) {
        append(buf, off, len)
    }

    /**
     * Clears content of this appender.
     */
    open fun clear() {
        val newNode = SNode<Any>()
        head = newNode
        cur = newNode
    }

    /**
     * Concatenate appended objects to [ByteArray].
     */
    open fun toByteArray(): ByteArray {
        // Computes size:
        var size = 0
        var n = head
        while (n.next !== null) {
            size += when (val v = n.value) {
                is ByteArray -> v.size
                is ByteBuffer -> v.remaining()
                is Byte -> 1
                else -> throw IllegalStateException("Unknown node value type: ${v?.javaClass}")
            }
            n = n.next!!
        }
        // Fills buffer
        val buffer = ByteArray(size)
        var i = 0
        n = head
        while (n.next !== null) {
            val s: Int = when (val v = n.value) {
                is ByteArray -> {
                    System.arraycopy(v, 0, buffer, i, v.size)
                    v.size
                }
                is ByteBuffer -> {
                    val remaining = v.remaining()
                    v.get(buffer, i, remaining)
                    remaining
                }
                is Byte -> {
                    buffer[i] = v
                    1
                }
                else -> throw IllegalStateException("Unknown node value type: ${v?.javaClass}")
            }
            i += s
            n = n.next!!
        }
        return buffer
    }

    override fun close() {
    }

    override fun flush() {
    }

    private fun flushBuffer() {
        val buf = buffer
        if (buf === null || bufferOffset == 0) {
            return
        }
        cur.value = buf.copyOfRange(0, bufferOffset)
        bufferOffset = 0
        newTail()
    }

    private fun newTail() {
        val newNode = SNode<Any>()
        cur.next = newNode
        cur = newNode
    }

    companion object {
        /**
         * Supplier to create new [BytesBuilder].
         */
        @JvmField
        val SUPPLIER: Supplier<BytesBuilder> = Supplier { BytesBuilder() }
    }
}

/**
 * List builder, used to append given objects and concatenate them to [List].
 */
open class ListBuilder<E> : Serializable {

    private var head = SNode<Any>()
    private var cur = head
    private var count = 0

    fun append(t: E?): ListBuilder<E> {
        cur.value = t
        newTail()
        return this
    }

    fun isEmpty(): Boolean {
        return head.next === null
    }

    private fun newTail() {
        val newNode = SNode<Any>()
        cur.next = newNode
        cur = newNode
        count++
    }

    /**
     *Concatenates appended elements to array.
     */
    open fun toArray(type: Class<E>): Array<E> {
        return toArray0(type)
    }

    /**
     * Concatenates appended elements to readonly [List].
     */
    open fun toList(): List<E> {
        val array = toArray0(Any::class.java)
        return array.asList().asType()
    }

    private fun <T> toArray0(type: Class<T>): Array<T> {
        val array = arrayOfType<Array<T>>(type, count)
        var n = head
        var i = 0
        while (n.next !== null) {
            array[i] = n.value.asType()
            i++
            n = n.next!!
        }
        return array
    }

    /**
     * Clears content of this appender.
     */
    open fun clear() {
        val newNode = SNode<Any>()
        head = newNode
        cur = newNode
    }
}
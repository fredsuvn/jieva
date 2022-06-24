package xyz.srclab.common.base

import java.io.Writer
import java.util.LinkedList

/**
 * String appender, used to append objects and finally join them into a [String].
 *
 * This appender is lazy, it stores the given appended object without other computation.
 * It computes join operation only when the [toString] method is called.
 */
open class StringAppender : SegmentAppender<StringAppender, CharSequence>, Appendable, Writer() {

    private val nodes: MutableList<Any?> = LinkedList()

    override fun append(t: CharSequence?): StringAppender {
        nodes.add(t)
        return this
    }

    override fun append(t: CharSequence, startIndex: Int): StringAppender {
        nodes.add(t.subRef(startIndex))
        return this
    }

    override fun append(t: CharSequence, startIndex: Int, endIndex: Int): StringAppender {
        return this
    }

    override fun append(c: Char): StringAppender {
        return append0(c)
    }

    open fun append(obj: Any?): StringAppender {
        return when (obj) {
            null -> append(defaultNullString())
            is CharSequence -> append0(obj)
            is CharArray -> append0(obj)
            is Char -> append0(obj)
            else -> append0(obj.toString())
        }
    }

    override fun write(c: Int) {
        append(c.toChar())
    }

    override fun write(cbuf: CharArray) {
        append0(cbuf)
    }

    override fun write(str: String) {
        append0(str)
    }

    override fun write(str: String, off: Int, len: Int) {
        append0(str.subRef(off, endIndex(off, len)))
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        append0(cbuf.subRef(off, endIndex(off, len)))
    }

    /**
     * Clears content.
     */
    open fun clear() {
        charCount = 0
        head.next = null
        tail = head
    }

    private fun append0(csq: CharSequence): StringAppender {
        val len = csq.length
        if (len == 0) {
            return this
        }
        val newNode = Node(csq.toString())
        charCount += len
        tail.next = newNode
        tail = newNode
        return this
    }

    private fun append0(chars: CharArray): StringAppender {
        val len = chars.size
        if (len == 0) {
            return this
        }
        val newNode = Node(chars.copyOf(len))
        charCount += len
        tail.next = newNode
        tail = newNode
        return this
    }

    private fun append0(c: Char): StringAppender {
        val newNode = Node(c)
        charCount += 1
        tail.next = newNode
        tail = newNode
        return this
    }

    /**
     * Builds appended parts as one [String].
     */
    override fun toString(): String {
        var curNode: Node? = head
        val charArray = CharArray(charCount)
        var i = 0
        while (curNode !== null) {
            val value = curNode.value
            var len = 0
            when (value) {
                is JavaString -> {
                    len = value.length
                    value.getChars(0, len, charArray, i)
                }
                is CharArray -> {
                    len = value.size
                    System.arraycopy(value, 0, charArray, i, len)
                }
                is Char -> {
                    len = 1
                    charArray[i] = value
                }
                else -> throw IllegalStateException("Unknown value type: ${value.javaClass}")
            }
            i += len
            curNode = curNode.next
        }
        val result = String(charArray)
        val newNode = Node(result)
        head.next = newNode
        tail = newNode
        return result
    }

    override fun close() {
    }

    override fun flush() {
    }

    private data class Node(
        val value: Any,
        var next: Node? = null,
    )
}

/**
 * Appender interface, used for appending.
 *
 * @param A type of this appender
 * @param T type of appended object
 */
interface Appender<A : Appender<A, T>, T> {

    /**
     * Appends and returns this.
     */
    fun append(t: T): A
}

/**
 * [Appender] of which appended type [T] is segmented.
 *
 * @param A type of this appender
 * @param T type of appended object
 */
interface SegmentAppender<A : SegmentAppender<A, T>, T> : Appender<A, T> {

    /**
     * Appends with [startIndex] and returns this.
     */
    fun append(t: T, startIndex: Int): A

    /**
     * Appends with [startIndex] and [endIndex] and returns this.
     */
    fun append(t: T, startIndex: Int, endIndex: Int): A
}
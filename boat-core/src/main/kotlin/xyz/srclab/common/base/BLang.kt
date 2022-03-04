@file:JvmName("BLang")

package xyz.srclab.common.base

import xyz.srclab.common.collect.arrayCopyOfRannge
import xyz.srclab.common.collect.arrayLength

/**
 * Gets or creates a new value.
 * This function is usually used for operation which checks the value is null, if it is not, return,
 * else create a new one then set and return.
 */
inline fun <T> getOrNew(
    lock: Any,
    getter: () -> T?,
    setter: (T) -> Unit,
    creator: () -> T
): T {
    val current = getter()
    if (current !== null) {
        return current
    }
    synchronized(lock) {
        val newOne = creator()
        setter(newOne)
        return newOne
    }
}

/**
 * Abstract object represents a final object, which will cache the values of [hashCode] and [toString].
 * The subclass should implement [hashCode0] and [toString0] to compute the values of [hashCode] and [toString],
 * each computation will be processed only once.
 */
abstract class FinalObject {

    private var _hashCode: Int? = null
    private var _toString: String? = null

    override fun hashCode(): Int {
        return getOrNew(
            this,
            { this._hashCode },
            { this._hashCode = it },
            { hashCode0() },
        )
    }

    override fun toString(): String {
        return getOrNew(
            this,
            { this._toString },
            { this._toString = it },
            { toString0() },
        )
    }

    /**
     * Computes the hash code.
     */
    protected abstract fun hashCode0(): Int

    /**
     * Computes the toString value.
     */
    protected abstract fun toString0(): String
}

/**
 * This class specifies a segment for an array of type [A].
 */
open class ArraySeg<A : Any> @JvmOverloads constructor(
    @get:JvmName("array") val array: A,
    @get:JvmName("offset") val offset: Int = 0,
    @get:JvmName("length") val length: Int = remainingLength(array.arrayLength(), offset),
) : FinalObject() {

    init {
        checkState(array.javaClass.isArray) { "Not an array: $array!" }
    }

    @get:JvmName("startIndex")
    val startIndex: Int = offset

    @get:JvmName("endIndex")
    val endIndex: Int = startIndex + length

    /**
     * Returns the absolute index of [array],
     * which is computed from given [index] -- a relative index of the offset of this segment.
     */
    fun absIndex(index: Int): Int {
        return offset + index
    }

    /**
     * Returns the copy of array range which is specified by this segment.
     */
    fun copyOfRange(): A {
        return array.arrayCopyOfRannge(startIndex, endIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ArraySeg<*>
        if (array != other.array) return false
        if (offset != other.offset) return false
        if (length != other.length) return false
        return true
    }

    override fun hashCode0(): Int {
        return toString().hashCode()
    }

    override fun toString0(): String {
        return "ArraySeg[$array, $offset, $length]"
    }
}
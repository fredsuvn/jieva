package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object ToString {

    @JvmStatic
    fun toString(any: Any?): String {
        return any.toString()
    }

    @JvmStatic
    fun deepToString(any: Any?): String {
        if (any == null) {
            return "null"
        }
        if (any is Array<*>) {
            return Arrays.deepToString(any)
        }
        if (any is BooleanArray) {
            return Arrays.toString(any)
        }
        if (any is ByteArray) {
            return Arrays.toString(any)
        }
        if (any is ShortArray) {
            return Arrays.toString(any)
        }
        if (any is CharArray) {
            return Arrays.toString(any)
        }
        if (any is IntArray) {
            return Arrays.toString(any)
        }
        if (any is LongArray) {
            return Arrays.toString(any)
        }
        if (any is FloatArray) {
            return Arrays.toString(any)
        }
        if (any is DoubleArray) {
            return Arrays.toString(any)
        }
        return any.toString()
    }
}
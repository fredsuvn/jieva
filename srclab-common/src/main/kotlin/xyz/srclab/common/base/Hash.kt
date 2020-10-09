package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object Hash {

    @JvmStatic
    fun hash(any: Any?): Int {
        return Objects.hashCode(any)
    }

    @JvmStatic
    fun hash(vararg objects: Any?): Int {
        return Objects.hash(objects)
    }

    @JvmStatic
    fun deepHash(any: Any?): Int {
        if (any == null) {
            return 0
        }
        if (any is Array<*>) {
            return Arrays.deepHashCode(any)
        }
        if (any is BooleanArray) {
            return Arrays.hashCode(any)
        }
        if (any is ByteArray) {
            return Arrays.hashCode(any)
        }
        if (any is ShortArray) {
            return Arrays.hashCode(any)
        }
        if (any is CharArray) {
            return Arrays.hashCode(any)
        }
        if (any is IntArray) {
            return Arrays.hashCode(any)
        }
        if (any is LongArray) {
            return Arrays.hashCode(any)
        }
        if (any is FloatArray) {
            return Arrays.hashCode(any)
        }
        if (any is DoubleArray) {
            return Arrays.hashCode(any)
        }
        return Objects.hashCode(any)
    }

    @JvmStatic
    fun deepHash(vararg objects: Any?): Int {
        return objects.contentDeepHashCode()
    }
}
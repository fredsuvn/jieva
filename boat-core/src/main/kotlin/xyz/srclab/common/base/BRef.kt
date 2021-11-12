package xyz.srclab.common.base

import java.util.function.Consumer

/**
 * A reference holds an object.
 *
 * It supports chain operation in Java:
 *
 * ```
 * Date dateValue = Ref.of(intValue)
 *     .apply(StringUtils::intToString)
 *     .accept(SystemUtils::printString)
 *     .get();
 * ```
 *
 * Same as:
 *
 * ```
 * String stringValue = StringUtils.intToString(intValue);
 * SystemUtils.printString(stringValue);
 * ```
 *
 * It also provides a mutable reference for final variable:
 *
 * ```
 * Ref<String> ref = Ref.of("1");
 * List<String> list = Arrays.asList("-1", "-2", "-3");
 * list.forEach(i -> ref.set(ref.get() + i));
 * ```
 */
interface BRef<T : Any> : BAccessor<T> {

    /**
     * If current value is `non-null`, given [action] will be executed, returns `this` with new value.
     * If current value is `null`, returns `this` with `null` value.
     */
    fun <R : Any> apply(action: (T) -> R): BRef<R> {
        val cur = getOrNull()
        if (cur === null) {
            return this.asTyped()
        }
        val thisRef = this.asTyped<BRef<R>>()
        thisRef.set(action(cur))
        return thisRef
    }

    /**
     * Given [action] will be executed, returns `this` with new value.
     */
    fun <R : Any> applyOrNull(action: (T?) -> R): BRef<R> {
        val thisRef = this.asTyped<BRef<R>>()
        thisRef.set(action(getOrNull()))
        return thisRef
    }

    /**
     * If current value is `non-null`, given [action] will be executed, returns `this`.
     */
    fun accept(action: Consumer<T>): BRef<T> {
        val cur = getOrNull()
        if (cur === null) {
            return this.asTyped()
        }
        action.accept(cur)
        return this
    }

    /**
     * Given [action] will be executed, returns `this`.
     */
    fun acceptOrNull(action: Consumer<T?>): BRef<T> {
        action.accept(getOrNull())
        return this
    }

    companion object {

        @JvmStatic
        fun <T : Any> of(obj: T?): BRef<T> {
            return BRefImpl(obj)
        }

        private class BRefImpl<T : Any>(private var value: T?) : BRef<T> {

            override fun getOrNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}

/**
 * Ref of an array.
 *
 * @param A array type
 */
interface BArrayRef<A : Any, E> : BRef<A> {

    /**
     * Returns source array.
     */
    val array: A

    val startIndex: Int
    val endIndex: Int
    val offset: Int get() = startIndex
    val length: Int get() = endIndex - startIndex

    operator fun get(index:Int):E

    operator fun set(index:Int, value:E)

    /**
     * Returns a new array which is a copy of current range.
     */
    fun copyOfRange(): A

    companion object {

        //@JvmOverloads
        //@JvmStatic
        //fun <T> Array<T>.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<Array<T>> {
        //    return object : BArrayRef<Array<T>> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun BooleanArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<BooleanArray> {
        //    return object : BArrayRef<BooleanArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun ByteArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<ByteArray> {
        //    return object : BArrayRef<ByteArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun CharArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<CharArray> {
        //    return object : BArrayRef<CharArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun ShortArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<ShortArray> {
        //    return object : BArrayRef<ShortArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun IntArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<IntArray> {
        //    return object : BArrayRef<IntArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun LongArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<LongArray> {
        //    return object : BArrayRef<LongArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun FloatArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<FloatArray> {
        //    return object : BArrayRef<FloatArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun DoubleArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<DoubleArray> {
        //    return object : BArrayRef<DoubleArray> {
        //        override val array = this@toArrayRef
        //        override val startIndex = startIndex
        //        override val endIndex = endIndex
        //    }
        //}
        //
        //@JvmOverloads
        //@JvmStatic
        //fun <A : Any> A.toArrayRef(
        //    startIndex: Int = 0,
        //    endIndex: Int = java.lang.reflect.Array.getLength(this)
        //): BArrayRef<A> {
        //    return when (this) {
        //        is BooleanArray -> toArrayRef(startIndex, endIndex)
        //        is ByteArray -> toArrayRef(startIndex, endIndex)
        //        is CharArray -> toArrayRef(startIndex, endIndex)
        //        is ShortArray -> toArrayRef(startIndex, endIndex)
        //        is IntArray -> toArrayRef(startIndex, endIndex)
        //        is LongArray -> toArrayRef(startIndex, endIndex)
        //        is FloatArray -> toArrayRef(startIndex, endIndex)
        //        is DoubleArray -> toArrayRef(startIndex, endIndex)
        //        is Array<*> -> toArrayRef(startIndex, endIndex)
        //        else -> throw IllegalStateException("Not an array: ${this.javaClass}")
        //    }.asTyped()
        //}
    }
}

open class
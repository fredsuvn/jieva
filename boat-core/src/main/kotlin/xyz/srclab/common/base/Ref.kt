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
 *     .with(DateUtils::stringToDate)
 *     .get();
 * ```
 *
 * Same as:
 *
 * ```
 * String stringValue = StringUtils.intToString(intValue);
 * SystemUtils.printString(stringValue);
 * Date dateValue = DateUtils.stringToDate(stringValue);
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
interface Ref<T : Any> : TypedAccessor<T> {

    /**
     * If value of this [Ref] is `non-null`, given [action] will be executed, else not.
     *
     * Returns [Ref] of result of execution, or [Ref] of `null` if not be executed.
     */
    fun <R : Any> apply(action: (T) -> R): Ref<R> {
        val v = getOrNull()
        return if (v === null) {
            null.toRef()
        } else {
            action(v).toRef()
        }
    }

    /**
     * Executes given [action], returns [Ref] of result of execution.
     */
    fun <R : Any> applyOrNull(action: (T?) -> R?): Ref<R> {
        return action(getOrNull()).toRef()
    }

    /**
     * If value of this [Ref] is `non-null`, given [action] will be executed, else not.
     *
     * Returns this.
     */
    fun accept(action: Consumer<T>): Ref<T> {
        val v = getOrNull()
        if (v !== null) {
            action.accept(v)
        }
        return this
    }

    /**
     * Executes given [action], returns this.
     */
    fun acceptOrNull(action: Consumer<T?>): Ref<T> {
        action.accept(getOrNull())
        return this
    }

    /**
     * If value of this [Ref] is `non-null`, given [action] will be executed, else not.
     *
     * Result of [action] execution will be set in this [Ref], and returns `this` itself as target generic type.
     */
    fun <R : Any> with(action: (T) -> R): Ref<R> {
        val v = getOrNull()
        val thisRef = this.asTyped<Ref<R>>()
        if (v === null) {
            thisRef.set(null)
        } else {
            thisRef.set(action(v))
        }
        return thisRef
    }

    /**
     * Executes given [action].
     *
     * Result of [action] execution will be set in this [Ref], and returns `this` itself as target generic type.
     */
    fun <R : Any> withOrNull(action: (T?) -> R?): Ref<R> {
        val v = getOrNull()
        val thisRef = this.asTyped<Ref<R>>()
        thisRef.set(action(v))
        return thisRef
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.toRef(): Ref<T> {
            return RefImpl(this)
        }

        private class RefImpl<T : Any>(
            private var value: T?
        ) : Ref<T> {

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
interface ArrayRef<A : Any> {

    /**
     * Returns source array.
     */
    val array: A

    val startIndex: Int
    val endIndex: Int
    val offset: Int get() = startIndex
    val length: Int get() = endIndex - startIndex

    /**
     * Returns a new array which is a copy of current range.
     */
    fun copyOfRange(): A {
        val arr = array
        return when (arr) {
            is BooleanArray -> arr.copyOfRange(startIndex, endIndex)
            is ByteArray -> arr.copyOfRange(startIndex, endIndex)
            is CharArray -> arr.copyOfRange(startIndex, endIndex)
            is ShortArray -> arr.copyOfRange(startIndex, endIndex)
            is IntArray -> arr.copyOfRange(startIndex, endIndex)
            is LongArray -> arr.copyOfRange(startIndex, endIndex)
            is FloatArray -> arr.copyOfRange(startIndex, endIndex)
            is DoubleArray -> arr.copyOfRange(startIndex, endIndex)
            is Array<*> -> arr.copyOfRange(startIndex, endIndex)
            else -> throw IllegalStateException("Not an array: ${array.javaClass}")
        }.asTyped()
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun <T> Array<T>.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<Array<T>> {
            return object : ArrayRef<Array<T>> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun BooleanArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<BooleanArray> {
            return object : ArrayRef<BooleanArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun ByteArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<ByteArray> {
            return object : ArrayRef<ByteArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun CharArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<CharArray> {
            return object : ArrayRef<CharArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun ShortArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<ShortArray> {
            return object : ArrayRef<ShortArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun IntArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<IntArray> {
            return object : ArrayRef<IntArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun LongArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<LongArray> {
            return object : ArrayRef<LongArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun FloatArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<FloatArray> {
            return object : ArrayRef<FloatArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun DoubleArray.toArrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<DoubleArray> {
            return object : ArrayRef<DoubleArray> {
                override val array = this@toArrayRef
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun <A : Any> A.toArrayRef(
            startIndex: Int = 0,
            endIndex: Int = java.lang.reflect.Array.getLength(this)
        ): ArrayRef<A> {
            return when (this) {
                is BooleanArray -> toArrayRef(startIndex, endIndex)
                is ByteArray -> toArrayRef(startIndex, endIndex)
                is CharArray -> toArrayRef(startIndex, endIndex)
                is ShortArray -> toArrayRef(startIndex, endIndex)
                is IntArray -> toArrayRef(startIndex, endIndex)
                is LongArray -> toArrayRef(startIndex, endIndex)
                is FloatArray -> toArrayRef(startIndex, endIndex)
                is DoubleArray -> toArrayRef(startIndex, endIndex)
                is Array<*> -> toArrayRef(startIndex, endIndex)
                else -> throw IllegalStateException("Not an array: ${this.javaClass}")
            }.asTyped()
        }
    }
}
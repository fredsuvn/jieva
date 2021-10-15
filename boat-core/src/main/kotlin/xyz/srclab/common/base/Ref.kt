package xyz.srclab.common.base

import xyz.srclab.common.base.Ref.Companion.toRef
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
 *
 * @see BooleanRef
 * @see ByteRef
 * @see ShortRef
 * @see CharRef
 * @see IntRef
 * @see LongRef
 * @see FloatRef
 * @see DoubleRef
 */
interface Ref<T : Any> : GenericAccessor<T> {

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
        val thisRef = this.asAny<Ref<R>>()
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
        val thisRef = this.asAny<Ref<R>>()
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
 * Boolean type of [Ref].
 */
interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    fun toObjectRef(): Ref<Boolean> {
        return get().toRef<Boolean>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Boolean.toRef(): BooleanRef {
            return BooleanRefImpl(this)
        }

        private class BooleanRefImpl(private var value: Boolean) : BooleanRef {

            override fun get(): Boolean {
                return value
            }

            override fun set(value: Boolean) {
                this.value = value
            }
        }
    }
}

/**
 * Byte type of [Ref].
 */
interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    fun toObjectRef(): Ref<Byte> {
        return get().toRef<Byte>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Byte.toRef(): ByteRef {
            return ByteRefImpl(this)
        }

        private class ByteRefImpl(private var value: Byte) : ByteRef {

            override fun get(): Byte {
                return value
            }

            override fun set(value: Byte) {
                this.value = value
            }
        }
    }
}

/**
 * Short type of [Ref].
 */
interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    fun toObjectRef(): Ref<Short> {
        return get().toRef<Short>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Short.toRef(): ShortRef {
            return ShortRefImpl(this)
        }

        private class ShortRefImpl(private var value: Short) : ShortRef {

            override fun get(): Short {
                return value
            }

            override fun set(value: Short) {
                this.value = value
            }
        }
    }
}

/**
 * Char type of [Ref].
 */
interface CharRef {

    fun get(): Char

    fun set(value: Char)

    fun toObjectRef(): Ref<Char> {
        return get().toRef<Char>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Char.toRef(): CharRef {
            return CharRefImpl(this)
        }

        private class CharRefImpl(private var value: Char) : CharRef {

            override fun get(): Char {
                return value
            }

            override fun set(value: Char) {
                this.value = value
            }
        }
    }
}

/**
 * Int type of [Ref].
 */
interface IntRef {

    fun get(): Int

    fun set(value: Int)

    fun toObjectRef(): Ref<Int> {
        return get().toRef<Int>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Int.toRef(): IntRef {
            return IntRefImpl(this)
        }

        private class IntRefImpl(private var value: Int) : IntRef {

            override fun get(): Int {
                return value
            }

            override fun set(value: Int) {
                this.value = value
            }
        }
    }
}

/**
 * Long type of [Ref].
 */
interface LongRef {

    fun get(): Long

    fun set(value: Long)

    fun toObjectRef(): Ref<Long> {
        return get().toRef<Long>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Long.toRef(): LongRef {
            return LongRefImpl(this)
        }

        private class LongRefImpl(private var value: Long) : LongRef {

            override fun get(): Long {
                return value
            }

            override fun set(value: Long) {
                this.value = value
            }
        }
    }
}

/**
 * Float type of [Ref].
 */
interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    fun toObjectRef(): Ref<Float> {
        return get().toRef<Float>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Float.toRef(): FloatRef {
            return FloatRefImpl(this)
        }

        private class FloatRefImpl(private var value: Float) : FloatRef {

            override fun get(): Float {
                return value
            }

            override fun set(value: Float) {
                this.value = value
            }
        }
    }
}

/**
 * Double type of [Ref].
 */
interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    fun toObjectRef(): Ref<Double> {
        return get().toRef<Double>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Double.toRef(): DoubleRef {
            return DoubleRefImpl(this)
        }

        private class DoubleRefImpl(private var value: Double) : DoubleRef {

            override fun get(): Double {
                return value
            }

            override fun set(value: Double) {
                this.value = value
            }
        }
    }
}

interface BaseArrayRef {
    val startIndex: Int
    val endIndex: Int
    val length: Int get() = endIndex - startIndex
}

/**
 * Ref of array.
 */
interface ArrayRef<T> : BaseArrayRef {

    val array: Array<T>

    companion object {

        @JvmOverloads
        @JvmStatic
        fun <T> of(array: Array<T>, startIndex: Int = 0, endIndex: Int = array.size): ArrayRef<T> {
            return object : ArrayRef<T> {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun <T> offset(array: Array<T>, offset: Int = 0, length: Int = array.size - offset): ArrayRef<T> {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of boolean array.
 */
interface BooleanArrayRef : BaseArrayRef {

    val array: BooleanArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: BooleanArray, startIndex: Int = 0, endIndex: Int = array.size): BooleanArrayRef {
            return object : BooleanArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: BooleanArray, offset: Int = 0, length: Int = array.size - offset): BooleanArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of byte array.
 */
interface ByteArrayRef : BaseArrayRef {

    val array: ByteArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: ByteArray, startIndex: Int = 0, endIndex: Int = array.size): ByteArrayRef {
            return object : ByteArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: ByteArray, offset: Int = 0, length: Int = array.size - offset): ByteArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of short array.
 */
interface ShortArrayRef : BaseArrayRef {

    val array: ShortArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: ShortArray, startIndex: Int = 0, endIndex: Int = array.size): ShortArrayRef {
            return object : ShortArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: ShortArray, offset: Int = 0, length: Int = array.size - offset): ShortArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of char array.
 */
interface CharArrayRef : BaseArrayRef {

    val array: CharArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: CharArray, startIndex: Int = 0, endIndex: Int = array.size): CharArrayRef {
            return object : CharArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: CharArray, offset: Int = 0, length: Int = array.size - offset): CharArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of int array.
 */
interface IntArrayRef : BaseArrayRef {

    val array: IntArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: IntArray, startIndex: Int = 0, endIndex: Int = array.size): IntArrayRef {
            return object : IntArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: IntArray, offset: Int = 0, length: Int = array.size - offset): IntArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of long array.
 */
interface LongArrayRef : BaseArrayRef {

    val array: LongArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: LongArray, startIndex: Int = 0, endIndex: Int = array.size): LongArrayRef {
            return object : LongArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: LongArray, offset: Int = 0, length: Int = array.size - offset): LongArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of float array.
 */
interface FloatArrayRef : BaseArrayRef {

    val array: FloatArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: FloatArray, startIndex: Int = 0, endIndex: Int = array.size): FloatArrayRef {
            return object : FloatArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: FloatArray, offset: Int = 0, length: Int = array.size - offset): FloatArrayRef {
            return of(array, offset, offset + length)
        }
    }
}

/**
 * Ref of double array.
 */
interface DoubleArrayRef : BaseArrayRef {

    val array: DoubleArray

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(array: DoubleArray, startIndex: Int = 0, endIndex: Int = array.size): DoubleArrayRef {
            return object : DoubleArrayRef {
                override val array = array
                override val startIndex = startIndex
                override val endIndex = endIndex
            }
        }

        @JvmOverloads
        @JvmStatic
        fun offset(array: DoubleArray, offset: Int = 0, length: Int = array.size - offset): DoubleArrayRef {
            return of(array, offset, offset + length)
        }
    }
}
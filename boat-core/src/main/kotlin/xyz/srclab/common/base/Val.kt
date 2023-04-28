package xyz.srclab.common.base

import java.util.*
import java.util.function.Function
import java.util.function.Supplier

/**
 * [Val] is a value wrapper, it is final and its value cannot be reassigned,
 * just like the kotlin keyword: `val`.
 *
 * @param T type of wrapped value
 */
interface Val<T : Any> {

    /**
     * Returns value of this wrapper.
     */
    fun get(): T?

    /**
     * Returns value of this wrapper, or [defaultValue] if the value is null.
     */
    fun orElse(defaultValue: T): T {
        return get() ?: defaultValue
    }

    /**
     * Returns value of this wrapper, or result of [supplier] if the value is null.
     */
    fun orElse(supplier: Supplier<T>): T {
        return get() ?: supplier.get()
    }

    /**
     * Maps current value by [func], returns a new [Val] to wrap the new value.
     */
    fun <R : Any> map(func: Function<in T?, out R?>): Val<R> {
        return func.apply(get()).toVal()
    }

    /**
     * Returns [Optional] from current value.
     */
    fun toOptional(): Optional<T> {
        return Optional.ofNullable(get())
    }

    companion object {

        /**
         * Returns a [Val] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.toVal(): Val<T> {
            return ValImpl(this)
        }

        private data class ValImpl<T : Any>(private val value: T?) : Val<T> {
            override fun get(): T? = value
        }
    }
}

/**
 * Boolean version of [Val].
 * @see Val
 */
interface BooleanVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Boolean

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Boolean> {
        return object : Val<Boolean> {
            override fun get(): Boolean = this@BooleanVal.get()
        }
    }

    companion object {

        /**
         * Returns a [BooleanVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Boolean.toVal(): BooleanVal {
            return BooleanValImpl(this)
        }

        private data class BooleanValImpl(private val value: Boolean) : BooleanVal {
            override fun get(): Boolean = value
        }
    }
}

/**
 * Byte version of [Val].
 * @see Val
 */
interface ByteVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Byte

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Byte> {
        return object : Val<Byte> {
            override fun get(): Byte = this@ByteVal.get()
        }
    }

    companion object {

        /**
         * Returns a [ByteVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Byte.toVal(): ByteVal {
            return ByteValImpl(this)
        }

        private data class ByteValImpl(private val value: Byte) : ByteVal {
            override fun get(): Byte = value
        }
    }
}

/**
 * Short version of [Val].
 * @see Val
 */
interface ShortVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Short

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Short> {
        return object : Val<Short> {
            override fun get(): Short = this@ShortVal.get()
        }
    }

    companion object {

        /**
         * Returns a [ShortVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Short.toVal(): ShortVal {
            return ShortValImpl(this)
        }

        private data class ShortValImpl(private val value: Short) : ShortVal {
            override fun get(): Short = value
        }
    }
}

/**
 * Char version of [Val].
 * @see Val
 */
interface CharVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Char

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Char> {
        return object : Val<Char> {
            override fun get(): Char = this@CharVal.get()
        }
    }

    companion object {

        /**
         * Returns a [CharVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Char.toVal(): CharVal {
            return CharValImpl(this)
        }

        private data class CharValImpl(private val value: Char) : CharVal {
            override fun get(): Char = value
        }
    }
}

/**
 * Int version of [Val].
 * @see Val
 */
interface IntVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Int

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Int> {
        return object : Val<Int> {
            override fun get(): Int = this@IntVal.get()
        }
    }

    companion object {

        /**
         * Returns a [IntVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Int.toVal(): IntVal {
            return IntValImpl(this)
        }

        private data class IntValImpl(private val value: Int) : IntVal {
            override fun get(): Int = value
        }
    }
}

/**
 * Long version of [Val].
 * @see Val
 */
interface LongVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Long

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Long> {
        return object : Val<Long> {
            override fun get(): Long = this@LongVal.get()
        }
    }

    companion object {

        /**
         * Returns a [LongVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Long.toVal(): LongVal {
            return LongValImpl(this)
        }

        private data class LongValImpl(private val value: Long) : LongVal {
            override fun get(): Long = value
        }
    }
}

/**
 * Float version of [Val].
 * @see Val
 */
interface FloatVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Float

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Float> {
        return object : Val<Float> {
            override fun get(): Float = this@FloatVal.get()
        }
    }

    companion object {

        /**
         * Returns a [FloatVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Float.toVal(): FloatVal {
            return FloatValImpl(this)
        }

        private data class FloatValImpl(private val value: Float) : FloatVal {
            override fun get(): Float = value
        }
    }
}

/**
 * Double version of [Val].
 * @see Val
 */
interface DoubleVal {

    /**
     * Returns value of this wrapper.
     */
    fun get(): Double

    /**
     * Returns this as [Val], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVal(): Val<Double> {
        return object : Val<Double> {
            override fun get(): Double = this@DoubleVal.get()
        }
    }

    companion object {

        /**
         * Returns a [DoubleVal] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Double.toVal(): DoubleVal {
            return DoubleValImpl(this)
        }

        private data class DoubleValImpl(private val value: Double) : DoubleVal {
            override fun get(): Double = value
        }
    }
}
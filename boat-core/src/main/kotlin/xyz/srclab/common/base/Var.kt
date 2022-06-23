package xyz.srclab.common.base

import java.util.function.Function

/**
 * [Var] represents a variable wrapper, of which [value] is a variable reference can be reassigned,
 * just like the kotlin keyword: `var`.
 *
 * @param T type of wrapped value
 */
interface Var<T> : Val<T> {

    /**
     * Value of this [Var].
     */
    override var value: T

    /**
     * Maps current [value] by [func], returns a new [Var] to wrap the new value.
     */
    override fun <R> map(func: Function<in T, out R>): Var<R> {
        return func.apply(value).toVar()
    }

    /**
     * Maps current [value] by [func], returns this [Var] to wrap the new value.
     *
     * Note: [map] method will create a new [Var] instance,
     * but [mapSet] returns this instance itself, it only changes the [value] and casts the generic type of [T].
     */
    fun <R> mapSet(func: Function<in T, out R>): Var<R> {
        val thisVar = this.asType<Var<R>>()
        thisVar.value = func.apply(value)
        return thisVar
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun <T> T.toVar(): Var<T> {
            return VarImpl(this)
        }

        private data class VarImpl<T>(override var value: T) : Var<T>
    }
}

/**
 * Boolean version of [Var].
 * @see Var
 */
interface BooleanVar : BooleanVal {

    /**
     * Value of this [Var].
     */
    override var value: Boolean

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Boolean> {
        return object : Var<Boolean> {
            override var value: Boolean
                get() = this@BooleanVar.value
                set(v) {
                    this@BooleanVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Boolean.toBooleanVar(): BooleanVar {
            return BooleanVarImpl(this)
        }

        private data class BooleanVarImpl(override var value: Boolean) : BooleanVar
    }
}

/**
 * Byte version of [Var].
 * @see Var
 */
interface ByteVar : ByteVal {

    /**
     * Value of this [Var].
     */
    override var value: Byte

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Byte> {
        return object : Var<Byte> {
            override var value: Byte
                get() = this@ByteVar.value
                set(v) {
                    this@ByteVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Byte.toByteVar(): ByteVar {
            return ByteVarImpl(this)
        }

        private data class ByteVarImpl(override var value: Byte) : ByteVar
    }
}

/**
 * Short version of [Var].
 * @see Var
 */
interface ShortVar : ShortVal {

    /**
     * Value of this [Var].
     */
    override var value: Short

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Short> {
        return object : Var<Short> {
            override var value: Short
                get() = this@ShortVar.value
                set(v) {
                    this@ShortVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Short.toShortVar(): ShortVar {
            return ShortVarImpl(this)
        }

        private data class ShortVarImpl(override var value: Short) : ShortVar
    }
}

/**
 * Char version of [Var].
 * @see Var
 */
interface CharVar : CharVal {

    /**
     * Value of this [Var].
     */
    override var value: Char

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Char> {
        return object : Var<Char> {
            override var value: Char
                get() = this@CharVar.value
                set(v) {
                    this@CharVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Char.toCharVar(): CharVar {
            return CharVarImpl(this)
        }

        private data class CharVarImpl(override var value: Char) : CharVar
    }
}

/**
 * Int version of [Var].
 * @see Var
 */
interface IntVar : IntVal {

    /**
     * Value of this [Var].
     */
    override var value: Int

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Int> {
        return object : Var<Int> {
            override var value: Int
                get() = this@IntVar.value
                set(v) {
                    this@IntVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Int.toIntVar(): IntVar {
            return IntVarImpl(this)
        }

        private data class IntVarImpl(override var value: Int) : IntVar
    }
}

/**
 * Long version of [Var].
 * @see Var
 */
interface LongVar : LongVal {

    /**
     * Value of this [Var].
     */
    override var value: Long

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Long> {
        return object : Var<Long> {
            override var value: Long
                get() = this@LongVar.value
                set(v) {
                    this@LongVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Long.toLongVar(): LongVar {
            return LongVarImpl(this)
        }

        private data class LongVarImpl(override var value: Long) : LongVar
    }
}

/**
 * Float version of [Var].
 * @see Var
 */
interface FloatVar : FloatVal {

    /**
     * Value of this [Var].
     */
    override var value: Float

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Float> {
        return object : Var<Float> {
            override var value: Float
                get() = this@FloatVar.value
                set(v) {
                    this@FloatVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Float.toFloatVar(): FloatVar {
            return FloatVarImpl(this)
        }

        private data class FloatVarImpl(override var value: Float) : FloatVar
    }
}

/**
 * Double version of [Var].
 * @see Var
 */
interface DoubleVar : DoubleVal {

    /**
     * Value of this [Var].
     */
    override var value: Double

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     */
    fun asVar(): Var<Double> {
        return object : Var<Double> {
            override var value: Double
                get() = this@DoubleVar.value
                set(v) {
                    this@DoubleVar.value = v
                }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Double.toDoubleVar(): DoubleVar {
            return DoubleVarImpl(this)
        }

        private data class DoubleVarImpl(override var value: Double) : DoubleVar
    }
}
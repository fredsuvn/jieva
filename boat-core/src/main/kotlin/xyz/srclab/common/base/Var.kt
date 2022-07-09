package xyz.srclab.common.base

import java.util.function.Function

/**
 * [Var] is a variable wrapper, its value can be gotten and set,
 * just like the kotlin keyword: `var`.
 *
 * @param T type of wrapped value
 */
interface Var<T : Any> : Val<T> {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: T?): T?

    /**
     * Maps current value by [func], returns a new [Var] to wrap the new value.
     */
    override fun <R : Any> map(func: Function<in T?, out R?>): Var<R> {
        return func.apply(get()).toVar()
    }

    /**
     * Maps current value by [func], returns this [Var] to wrap the new value.
     *
     * Note: [map] method will create a new [Var] instance,
     * but [mapSet] returns this instance itself, it only changes the value and casts the generic type of [T].
     */
    fun <R : Any> mapSet(func: Function<in T?, out R?>): Var<R> {
        val thisVar = this.asType<Var<R>>()
        thisVar.set(func.apply(get()))
        return thisVar
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.toVar(): Var<T> {
            return VarImpl(this)
        }

        private data class VarImpl<T : Any>(private var value: T?) : Var<T> {

            override fun get(): T? = value

            override fun set(value: T?): T? {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Boolean version of [Var].
 * @see Var
 */
interface BooleanVar : BooleanVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Boolean): Boolean

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `false`.
     */
    fun asVar(): Var<Boolean> {
        return object : Var<Boolean> {

            override fun get(): Boolean = this@BooleanVar.get()

            override fun set(value: Boolean?): Boolean? {
                val old = this@BooleanVar.get()
                this@BooleanVar.set(value ?: false)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Boolean.toVar(): BooleanVar {
            return BooleanVarImpl(this)
        }

        private data class BooleanVarImpl(private var value: Boolean) : BooleanVar {

            override fun get(): Boolean = value

            override fun set(value: Boolean): Boolean {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Byte version of [Var].
 * @see Var
 */
interface ByteVar : ByteVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Byte): Byte

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Byte> {
        return object : Var<Byte> {

            override fun get(): Byte = this@ByteVar.get()

            override fun set(value: Byte?): Byte? {
                val old = this@ByteVar.get()
                this@ByteVar.set(value ?: 0)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Byte.toVar(): ByteVar {
            return ByteVarImpl(this)
        }

        private data class ByteVarImpl(private var value: Byte) : ByteVar {

            override fun get(): Byte = value

            override fun set(value: Byte): Byte {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Short version of [Var].
 * @see Var
 */
interface ShortVar : ShortVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Short): Short

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Short> {
        return object : Var<Short> {

            override fun get(): Short = this@ShortVar.get()

            override fun set(value: Short?): Short? {
                val old = this@ShortVar.get()
                this@ShortVar.set(value ?: 0)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Short.toVar(): ShortVar {
            return ShortVarImpl(this)
        }

        private data class ShortVarImpl(private var value: Short) : ShortVar {

            override fun get(): Short = value

            override fun set(value: Short): Short {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Char version of [Var].
 * @see Var
 */
interface CharVar : CharVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Char): Char

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Char> {
        return object : Var<Char> {

            override fun get(): Char = this@CharVar.get()

            override fun set(value: Char?): Char? {
                val old = this@CharVar.get()
                this@CharVar.set(value ?: 0.toChar())
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Char.toVar(): CharVar {
            return CharVarImpl(this)
        }

        private data class CharVarImpl(private var value: Char) : CharVar {

            override fun get(): Char = value

            override fun set(value: Char): Char {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Int version of [Var].
 * @see Var
 */
interface IntVar : IntVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Int): Int

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Int> {
        return object : Var<Int> {

            override fun get(): Int = this@IntVar.get()

            override fun set(value: Int?): Int? {
                val old = this@IntVar.get()
                this@IntVar.set(value ?: 0)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Int.toVar(): IntVar {
            return IntVarImpl(this)
        }

        private data class IntVarImpl(private var value: Int) : IntVar {

            override fun get(): Int = value

            override fun set(value: Int): Int {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Long version of [Var].
 * @see Var
 */
interface LongVar : LongVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Long): Long

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Long> {
        return object : Var<Long> {

            override fun get(): Long = this@LongVar.get()

            override fun set(value: Long?): Long? {
                val old = this@LongVar.get()
                this@LongVar.set(value ?: 0)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Long.toVar(): LongVar {
            return LongVarImpl(this)
        }

        private data class LongVarImpl(private var value: Long) : LongVar {

            override fun get(): Long = value

            override fun set(value: Long): Long {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Float version of [Var].
 * @see Var
 */
interface FloatVar : FloatVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Float): Float

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Float> {
        return object : Var<Float> {

            override fun get(): Float = this@FloatVar.get()

            override fun set(value: Float?): Float? {
                val old = this@FloatVar.get()
                this@FloatVar.set(value ?: 0.0f)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Float.toVar(): FloatVar {
            return FloatVarImpl(this)
        }

        private data class FloatVarImpl(private var value: Float) : FloatVar {

            override fun get(): Float = value

            override fun set(value: Float): Float {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}

/**
 * Double version of [Var].
 * @see Var
 */
interface DoubleVar : DoubleVal {

    /**
     * Sets new value amd returns old value of this wrapper.
     */
    fun set(value: Double): Double

    /**
     * Returns this as [Var], they are equivalent and have same status, any operation will affect each other.
     *
     * Note set `null` value will be seen as set `0`.
     */
    fun asVar(): Var<Double> {
        return object : Var<Double> {

            override fun get(): Double = this@DoubleVar.get()

            override fun set(value: Double?): Double? {
                val old = this@DoubleVar.get()
                this@DoubleVar.set(value ?: 0.0)
                return old
            }
        }
    }

    companion object {

        /**
         * Returns a [Var] with [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Double.toVar(): DoubleVar {
            return DoubleVarImpl(this)
        }

        private data class DoubleVarImpl(private var value: Double) : DoubleVar {

            override fun get(): Double = value

            override fun set(value: Double): Double {
                val old = this.value
                this.value = value
                return old
            }
        }
    }
}
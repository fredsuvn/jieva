package xyz.srclab.common.base

/**
 * [Var] represents a variable wrapper, of which [value] is a variable reference can be reassigned,
 * just like the kotlin keyword: `var`.
 */
interface Var<T> {

    /**
     * Value of this [Var].
     */
    var value: T

    companion object {

        /**
         * Returns a [Var] with [value].
         */
        @JvmStatic
        fun <T> of(value: T): Var<T> {
            return VarImpl(value)
        }

        private data class VarImpl<T>(override var value: T) : Var<T>
    }
}

/**
 * Boolean version of [Var].
 * @see Var
 */
interface BooleanVar {

    /**
     * Value of this [Var].
     */
    var value: Boolean

    companion object {

        /**
         * Returns a [BooleanVar] with [value].
         */
        @JvmStatic
        fun of(value: Boolean): BooleanVar {
            return BooleanVarImpl(value)
        }

        private data class BooleanVarImpl(override var value: Boolean) : BooleanVar
    }
}

/**
 * Byte version of [Var].
 * @see Var
 */
interface ByteVar {

    /**
     * Value of this [Var].
     */
    var value: Byte

    companion object {

        /**
         * Returns a [ByteVar] with [value].
         */
        @JvmStatic
        fun of(value: Byte): ByteVar {
            return ByteVarImpl(value)
        }

        private data class ByteVarImpl(override var value: Byte) : ByteVar
    }
}

/**
 * Short version of [Var].
 * @see Var
 */
interface ShortVar {

    /**
     * Value of this [Var].
     */
    var value: Short

    companion object {

        /**
         * Returns a [ShortVar] with [value].
         */
        @JvmStatic
        fun of(value: Short): ShortVar {
            return ShortVarImpl(value)
        }

        private data class ShortVarImpl(override var value: Short) : ShortVar
    }
}

/**
 * Char version of [Var].
 * @see Var
 */
interface CharVar {

    /**
     * Value of this [Var].
     */
    var value: Char

    companion object {

        /**
         * Returns a [CharVar] with [value].
         */
        @JvmStatic
        fun of(value: Char): CharVar {
            return CharVarImpl(value)
        }

        private data class CharVarImpl(override var value: Char) : CharVar
    }
}

/**
 * Int version of [Var].
 * @see Var
 */
interface IntVar {

    /**
     * Value of this [Var].
     */
    var value: Int

    companion object {

        /**
         * Returns a [IntVar] with [value].
         */
        @JvmStatic
        fun of(value: Int): IntVar {
            return IntVarImpl(value)
        }

        private data class IntVarImpl(override var value: Int) : IntVar
    }
}

/**
 * Long version of [Var].
 * @see Var
 */
interface LongVar {

    /**
     * Value of this [Var].
     */
    var value: Long

    companion object {

        /**
         * Returns a [LongVar] with [value].
         */
        @JvmStatic
        fun of(value: Long): LongVar {
            return LongVarImpl(value)
        }

        private data class LongVarImpl(override var value: Long) : LongVar
    }
}

/**
 * Float version of [Var].
 * @see Var
 */
interface FloatVar {

    /**
     * Value of this [Var].
     */
    var value: Float

    companion object {

        /**
         * Returns a [FloatVar] with [value].
         */
        @JvmStatic
        fun of(value: Float): FloatVar {
            return FloatVarImpl(value)
        }

        private data class FloatVarImpl(override var value: Float) : FloatVar
    }
}

/**
 * Double version of [Var].
 * @see Var
 */
interface DoubleVar {

    /**
     * Value of this [Var].
     */
    var value: Double

    companion object {

        /**
         * Returns a [DoubleVar] with [value].
         */
        @JvmStatic
        fun of(value: Double): DoubleVar {
            return DoubleVarImpl(value)
        }

        private data class DoubleVarImpl(override var value: Double) : DoubleVar
    }
}
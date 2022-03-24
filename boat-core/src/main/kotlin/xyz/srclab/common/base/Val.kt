package xyz.srclab.common.base

/**
 * [Val] represents a value wrapper, of which [value] is a final reference cannot be reassigned,
 * just like the kotlin keyword: `val`.
 */
interface Val<T> {

    /**
     * Value of this [Val].
     */
    val value: T

    companion object {

        /**
         * Returns a [Val] with [value].
         */
        @JvmStatic
        fun <T> of(value: T): Val<T> {
            return ValImpl(value)
        }

        private data class ValImpl<T>(override val value: T) : Val<T>
    }
}

/**
 * Boolean version of [Val].
 * @see Val
 */
interface BooleanVal {

    /**
     * Value of this [Val].
     */
    val value: Boolean

    companion object {

        /**
         * Returns a [BooleanVal] with [value].
         */
        @JvmStatic
        fun of(value: Boolean): BooleanVal {
            return BooleanValImpl(value)
        }

        private data class BooleanValImpl(override val value: Boolean) : BooleanVal
    }
}

/**
 * Byte version of [Val].
 * @see Val
 */
interface ByteVal {

    /**
     * Value of this [Val].
     */
    val value: Byte

    companion object {

        /**
         * Returns a [ByteVal] with [value].
         */
        @JvmStatic
        fun of(value: Byte): ByteVal {
            return ByteValImpl(value)
        }

        private data class ByteValImpl(override val value: Byte) : ByteVal
    }
}

/**
 * Short version of [Val].
 * @see Val
 */
interface ShortVal {

    /**
     * Value of this [Val].
     */
    val value: Short

    companion object {

        /**
         * Returns a [ShortVal] with [value].
         */
        @JvmStatic
        fun of(value: Short): ShortVal {
            return ShortValImpl(value)
        }

        private data class ShortValImpl(override val value: Short) : ShortVal
    }
}

/**
 * Char version of [Val].
 * @see Val
 */
interface CharVal {

    /**
     * Value of this [Val].
     */
    val value: Char

    companion object {

        /**
         * Returns a [CharVal] with [value].
         */
        @JvmStatic
        fun of(value: Char): CharVal {
            return CharValImpl(value)
        }

        private data class CharValImpl(override val value: Char) : CharVal
    }
}

/**
 * Int version of [Val].
 * @see Val
 */
interface IntVal {

    /**
     * Value of this [Val].
     */
    val value: Int

    companion object {

        /**
         * Returns a [IntVal] with [value].
         */
        @JvmStatic
        fun of(value: Int): IntVal {
            return IntValImpl(value)
        }

        private data class IntValImpl(override val value: Int) : IntVal
    }
}

/**
 * Long version of [Val].
 * @see Val
 */
interface LongVal {

    /**
     * Value of this [Val].
     */
    val value: Long

    companion object {

        /**
         * Returns a [LongVal] with [value].
         */
        @JvmStatic
        fun of(value: Long): LongVal {
            return LongValImpl(value)
        }

        private data class LongValImpl(override val value: Long) : LongVal
    }
}

/**
 * Float version of [Val].
 * @see Val
 */
interface FloatVal {

    /**
     * Value of this [Val].
     */
    val value: Float

    companion object {

        /**
         * Returns a [FloatVal] with [value].
         */
        @JvmStatic
        fun of(value: Float): FloatVal {
            return FloatValImpl(value)
        }

        private data class FloatValImpl(override val value: Float) : FloatVal
    }
}

/**
 * Double version of [Val].
 * @see Val
 */
interface DoubleVal {

    /**
     * Value of this [Val].
     */
    val value: Double

    companion object {

        /**
         * Returns a [DoubleVal] with [value].
         */
        @JvmStatic
        fun of(value: Double): DoubleVal {
            return DoubleValImpl(value)
        }

        private data class DoubleValImpl(override val value: Double) : DoubleVal
    }
}
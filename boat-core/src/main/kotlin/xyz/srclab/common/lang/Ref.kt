package xyz.srclab.common.lang

/**
 * A container holds an object.
 * Can help solve the problem like that:
 * a local variable should be final if it will be used in lambda expression (in java):
 * ```
 * Ref<String> ref = Ref.of("1");
 * List<String> list = Arrays.asList("-1", "-2", "-3");
 *
 * //here <String> should be final without Ref
 * list.forEach(i -> ref.set(ref.get() + i));
 * ```
 */
interface Ref<T : Any> : GenericAccessor<T> {

    companion object {

        @JvmStatic
        fun <T : Any> T?.of(): Ref<T> {
            return RefImpl(this)
        }

        private class RefImpl<T : Any>(private var value: T?) : Ref<T> {

            override fun getOrNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}

interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    companion object {

        @JvmStatic
        fun Boolean.of(): BooleanRef {
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

interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    companion object {

        @JvmStatic
        fun Byte.of(): ByteRef {
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

interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    companion object {

        @JvmStatic
        fun Short.of(): ShortRef {
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

interface CharRef {

    fun get(): Char

    fun set(value: Char)

    companion object {

        @JvmStatic
        fun Char.of(): CharRef {
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

interface IntRef {

    fun get(): Int

    fun set(value: Int)

    companion object {

        @JvmStatic
        fun Int.of(): IntRef {
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

interface LongRef {

    fun get(): Long

    fun set(value: Long)

    companion object {

        @JvmStatic
        fun Long.of(): LongRef {
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

interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    companion object {

        @JvmStatic
        fun Float.of(): FloatRef {
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

interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    companion object {

        @JvmStatic
        fun Double.of(): DoubleRef {
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
package xyz.srclab.common.lang

/**
 * A container holds an object.
 * Can help solve the problem like that a local variable is final if it is used in lambda expression (in java):
 *
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

        @JvmName("with")
        @JvmStatic
        fun <T : Any> T?.withRef(): Ref<T> {
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

        @JvmName("with")
        @JvmStatic
        fun Boolean.withRef(): BooleanRef {
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

        @JvmName("with")
        @JvmStatic
        fun Byte.withRef(): ByteRef {
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

        @JvmName("with")
        @JvmStatic
        fun Short.withRef(): ShortRef {
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

        @JvmName("with")
        @JvmStatic
        fun Char.withRef(): CharRef {
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

        @JvmName("with")
        @JvmStatic
        fun Int.withRef(): IntRef {
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

        @JvmName("with")
        @JvmStatic
        fun Long.withRef(): LongRef {
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

        @JvmName("with")
        @JvmStatic
        fun Float.withRef(): FloatRef {
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

        @JvmName("with")
        @JvmStatic
        fun Double.withRef(): DoubleRef {
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
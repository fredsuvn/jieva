package xyz.srclab.common.base

interface Ref<T> : SimpleAccessor<T> {

    companion object {

        @JvmStatic
        fun <T> of(initial: T?): Ref<T> {
            return RefImpl(initial)
        }

        private class RefImpl<T>(private var value: T?) : Ref<T> {

            override fun getOrNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}

fun <T> T.ref(): Ref<T> {
    return Ref.of(this)
}

interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    companion object {

        @JvmStatic
        fun of(initial: Boolean): BooleanRef {
            return BooleanRefImpl(initial)
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

fun Boolean.ref(): BooleanRef {
    return BooleanRef.of(this)
}

interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    companion object {

        @JvmStatic
        fun of(initial: Byte): ByteRef {
            return ByteRefImpl(initial)
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

fun Byte.ref(): ByteRef {
    return ByteRef.of(this)
}

interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    companion object {

        @JvmStatic
        fun of(initial: Short): ShortRef {
            return ShortRefImpl(initial)
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

fun Short.ref(): ShortRef {
    return ShortRef.of(this)
}

interface CharRef {

    fun get(): Char

    fun set(value: Char)

    companion object {

        @JvmStatic
        fun of(initial: Char): CharRef {
            return CharRefImpl(initial)
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

fun Char.ref(): CharRef {
    return CharRef.of(this)
}

interface IntRef {

    fun get(): Int

    fun set(value: Int)

    companion object {

        @JvmStatic
        fun of(initial: Int): IntRef {
            return IntRefImpl(initial)
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

fun Int.ref(): IntRef {
    return IntRef.of(this)
}

interface LongRef {

    fun get(): Long

    fun set(value: Long)

    companion object {

        @JvmStatic
        fun of(initial: Long): LongRef {
            return LongRefImpl(initial)
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

fun Long.ref(): LongRef {
    return LongRef.of(this)
}

interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    companion object {

        @JvmStatic
        fun of(initial: Float): FloatRef {
            return FloatRefImpl(initial)
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

fun Float.ref(): FloatRef {
    return FloatRef.of(this)
}

interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    companion object {

        @JvmStatic
        fun of(initial: Double): DoubleRef {
            return DoubleRefImpl(initial)
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

fun Double.ref(): DoubleRef {
    return DoubleRef.of(this)
}
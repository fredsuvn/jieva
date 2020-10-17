package xyz.srclab.common.base

interface Ref<T : Any> {

    @Throws(NullPointerException::class)
    fun get(): T {
        return getOrNull() ?: throw NullPointerException()
    }

    fun getOrNull(): T?

    fun set(value: T?)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> of(initial: T? = null): Ref<T> {
            return RefImpl(initial)
        }
    }
}

private class RefImpl<T : Any>(private var value: T?) : Ref<T> {

    override fun getOrNull(): T? {
        return value
    }

    override fun set(value: T?) {
        this.value = value
    }
}

fun <T : Any> refOf(initial: T? = null): Ref<T> {
    return Ref.of(initial)
}

interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    companion object {

        @JvmStatic
        fun of(initial: Boolean = false): BooleanRef {
            return BooleanRefImpl(initial)
        }
    }
}

private class BooleanRefImpl(private var value: Boolean) : BooleanRef {

    override fun get(): Boolean {
        return value
    }

    override fun set(value: Boolean) {
        this.value = value
    }
}

fun booleanRefOf(initial: Boolean = false): BooleanRef {
    return BooleanRef.of(initial)
}

interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    companion object {

        @JvmStatic
        fun of(initial: Byte = 0): ByteRef {
            return ByteRefImpl(initial)
        }
    }
}

private class ByteRefImpl(private var value: Byte) : ByteRef {

    override fun get(): Byte {
        return value
    }

    override fun set(value: Byte) {
        this.value = value
    }
}

fun byteRefOf(initial: Byte = 0): ByteRef {
    return ByteRef.of(initial)
}

interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    companion object {

        @JvmStatic
        fun of(initial: Short = 0): ShortRef {
            return ShortRefImpl(initial)
        }
    }
}

private class ShortRefImpl(private var value: Short) : ShortRef {

    override fun get(): Short {
        return value
    }

    override fun set(value: Short) {
        this.value = value
    }
}

fun shortRefOf(initial: Short = 0): ShortRef {
    return ShortRef.of(initial)
}

interface CharRef {

    fun get(): Char

    fun set(value: Char)

    companion object {

        @JvmStatic
        fun of(initial: Char = 0.toChar()): CharRef {
            return CharRefImpl(initial)
        }
    }
}

private class CharRefImpl(private var value: Char) : CharRef {

    override fun get(): Char {
        return value
    }

    override fun set(value: Char) {
        this.value = value
    }
}

fun charRefOf(initial: Char = 0.toChar()): CharRef {
    return CharRef.of(initial)
}

interface IntRef {

    fun get(): Int

    fun set(value: Int)

    companion object {

        @JvmStatic
        fun of(initial: Int = 0): IntRef {
            return IntRefImpl(initial)
        }
    }
}

private class IntRefImpl(private var value: Int) : IntRef {

    override fun get(): Int {
        return value
    }

    override fun set(value: Int) {
        this.value = value
    }
}

fun intRefOf(initial: Int = 0): IntRef {
    return IntRef.of(initial)
}

interface LongRef {

    fun get(): Long

    fun set(value: Long)

    companion object {

        @JvmStatic
        fun of(initial: Long = 0L): LongRef {
            return LongRefImpl(initial)
        }
    }
}

private class LongRefImpl(private var value: Long) : LongRef {

    override fun get(): Long {
        return value
    }

    override fun set(value: Long) {
        this.value = value
    }
}

fun longRefOf(initial: Long = 0L): LongRef {
    return LongRef.of(initial)
}

interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    companion object {

        @JvmStatic
        fun of(initial: Float = 0f): FloatRef {
            return FloatRefImpl(initial)
        }
    }
}

private class FloatRefImpl(private var value: Float) : FloatRef {

    override fun get(): Float {
        return value
    }

    override fun set(value: Float) {
        this.value = value
    }
}

fun flotRefOf(initial: Float = 0f): FloatRef {
    return FloatRef.of(initial)
}

interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    companion object {

        @JvmStatic
        fun of(initial: Double = 0.0): DoubleRef {
            return DoubleRefImpl(initial)
        }
    }
}

private class DoubleRefImpl(private var value: Double) : DoubleRef {

    override fun get(): Double {
        return value
    }

    override fun set(value: Double) {
        this.value = value
    }
}

fun doubleRefOf(initial: Double = 0.0): DoubleRef {
    return DoubleRef.of(initial)
}
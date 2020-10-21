package xyz.srclab.common.base

interface Ref<T> {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val isPresent: Boolean
        @JvmName("isPresent") get() {
            return getOrNull() !== null
        }

    @Throws(NullPointerException::class)
    fun get(): T {
        return getOrNull() ?: throw NullPointerException()
    }

    fun getOrNull(): T?

    fun set(value: T?)

    fun orElse(value: T): T {
        return getOrNull() ?: value
    }

    fun orElseGet(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    fun orElseThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T> of(initial: T? = null): Ref<T> {
            return RefImpl(initial)
        }
    }
}

fun <T> T.ref(): Ref<T> {
    return Ref.of(this)
}

fun <T> refOf(initial: T? = null): Ref<T> {
    return Ref.of(initial)
}

private class RefImpl<T>(private var value: T?) : Ref<T> {

    override fun getOrNull(): T? {
        return value
    }

    override fun set(value: T?) {
        this.value = value
    }
}

interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Boolean = false): BooleanRef {
            return BooleanRefImpl(initial)
        }
    }
}

fun Boolean.ref(): BooleanRef {
    return BooleanRef.of(this)
}

fun booleanRefOf(initial: Boolean = false): BooleanRef {
    return BooleanRef.of(initial)
}

private class BooleanRefImpl(private var value: Boolean) : BooleanRef {

    override fun get(): Boolean {
        return value
    }

    override fun set(value: Boolean) {
        this.value = value
    }
}

interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Byte = 0): ByteRef {
            return ByteRefImpl(initial)
        }
    }
}

fun Byte.ref(): ByteRef {
    return ByteRef.of(this)
}

fun byteRefOf(initial: Byte = 0): ByteRef {
    return ByteRef.of(initial)
}

private class ByteRefImpl(private var value: Byte) : ByteRef {

    override fun get(): Byte {
        return value
    }

    override fun set(value: Byte) {
        this.value = value
    }
}

interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Short = 0): ShortRef {
            return ShortRefImpl(initial)
        }
    }
}

fun Short.ref(): ShortRef {
    return ShortRef.of(this)
}

fun shortRefOf(initial: Short = 0): ShortRef {
    return ShortRef.of(initial)
}

private class ShortRefImpl(private var value: Short) : ShortRef {

    override fun get(): Short {
        return value
    }

    override fun set(value: Short) {
        this.value = value
    }
}

interface CharRef {

    fun get(): Char

    fun set(value: Char)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Char = 0.toChar()): CharRef {
            return CharRefImpl(initial)
        }
    }
}

fun Char.ref(): CharRef {
    return CharRef.of(this)
}

fun charRefOf(initial: Char = 0.toChar()): CharRef {
    return CharRef.of(initial)
}

private class CharRefImpl(private var value: Char) : CharRef {

    override fun get(): Char {
        return value
    }

    override fun set(value: Char) {
        this.value = value
    }
}

interface IntRef {

    fun get(): Int

    fun set(value: Int)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Int = 0): IntRef {
            return IntRefImpl(initial)
        }
    }
}

fun Int.ref(): IntRef {
    return IntRef.of(this)
}

fun intRefOf(initial: Int = 0): IntRef {
    return IntRef.of(initial)
}

private class IntRefImpl(private var value: Int) : IntRef {

    override fun get(): Int {
        return value
    }

    override fun set(value: Int) {
        this.value = value
    }
}

interface LongRef {

    fun get(): Long

    fun set(value: Long)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Long = 0L): LongRef {
            return LongRefImpl(initial)
        }
    }
}

fun Long.ref(): LongRef {
    return LongRef.of(this)
}

fun longRefOf(initial: Long = 0L): LongRef {
    return LongRef.of(initial)
}

private class LongRefImpl(private var value: Long) : LongRef {

    override fun get(): Long {
        return value
    }

    override fun set(value: Long) {
        this.value = value
    }
}

interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Float = 0f): FloatRef {
            return FloatRefImpl(initial)
        }
    }
}

fun Float.ref(): FloatRef {
    return FloatRef.of(this)
}

fun flotRefOf(initial: Float = 0f): FloatRef {
    return FloatRef.of(initial)
}

private class FloatRefImpl(private var value: Float) : FloatRef {

    override fun get(): Float {
        return value
    }

    override fun set(value: Float) {
        this.value = value
    }
}

interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(initial: Double = 0.0): DoubleRef {
            return DoubleRefImpl(initial)
        }
    }
}

fun Double.ref(): DoubleRef {
    return DoubleRef.of(this)
}

fun doubleRefOf(initial: Double = 0.0): DoubleRef {
    return DoubleRef.of(initial)
}

private class DoubleRefImpl(private var value: Double) : DoubleRef {

    override fun get(): Double {
        return value
    }

    override fun set(value: Double) {
        this.value = value
    }
}
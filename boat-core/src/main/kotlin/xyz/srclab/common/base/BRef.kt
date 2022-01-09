@file:JvmName("BRef")

package xyz.srclab.common.base

import xyz.srclab.annotations.ForJava
import xyz.srclab.common.base.Ref.Companion.ref
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

fun <T : Any> Optional<T>.toRef(): Ref<T> {
    return this.orElse(null).ref()
}

/**
 * Presents a reference holds a value.
 *
 * It supports chain operation in Java:
 *
 * ```
 * Date dateValue = Ref.of(intValue)
 *     .map(StringUtils::intToString)
 *     .accept(SystemUtils::printString)
 *     .get();
 * ```
 *
 * Same as:
 *
 * ```
 * String stringValue = StringUtils.intToString(intValue);
 * SystemUtils.printString(stringValue);
 * ```
 *
 * It also provides mutable operation for final variable:
 *
 * ```
 * Ref<String> ref = Ref.of("a");
 * List<String> list = Arrays.asList("b", "c", "d");
 * list.forEach(i -> ref.set(ref.get() + "-" + i));
 * SystemUtils.printString(ref.get());//a-b-c-d
 * ```
 *
 * This interface is similar to [Optional] in chain operations, the differences are:
 *
 * * [Ref] is mutable but [Optional] is immutable;
 * * [Ref] always return itself (although generic type may be changed) but [Optional] return a new object;
 */
@ForJava
interface Ref<T : Any> : GetRef<T>, SetRef<T> {

    /**
     * If current value is null, sets null, else sets result of [func]. Returns this as typed.
     */
    fun <R : Any> map(func: Function<T, R?>): Ref<R> {
        val value = orNull()
        return if (value === null) {
            set(null)
            this.asTyped()
        } else {
            val result: Ref<R> = this.asTyped()
            result.set(func.apply(value))
            result
        }
    }

    /**
     * Sets result of [func] whatever current value is null or null. Returns this as typed.
     */
    fun <R : Any> mapNullable(func: Function<T?, R?>): Ref<R> {
        val result: Ref<R> = this.asTyped()
        result.set(func.apply(orNull()))
        return result
    }

    /**
     * If current value is null, does nothing, else calls [consumer]. Returns this.
     */
    fun <R : Any> accept(consumer: Consumer<T>) = apply {
        val value = orNull()
        if (value !== null) {
            consumer.accept(value)
        }
    }

    /**
     * Calls [consumer] whatever current value is null or null. Returns this.
     */
    fun <R : Any> acceptNullable(consumer: Consumer<T?>) = apply {
        consumer.accept(orNull())
    }

    fun copy(): Ref<T> {
        return orNull().ref()
    }

    fun toOptional(): Optional<T> {
        return Optional.ofNullable(orNull())
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.ref(): Ref<T> {
            return RefImpl(this)
        }

        private class RefImpl<T : Any>(private var value: T?) : Ref<T> {

            override fun orNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}

interface GetRef<T : Any> {

    val isPresent: Boolean
        get() {
            return orNull() !== null
        }

    @Throws(NullPointerException::class)
    fun get(): T {
        return orNull()!!
    }

    fun orNull(): T?

    fun orDefault(value: T): T {
        return orNull() ?: value
    }

    fun orElse(value: Supplier<T>): T {
        return orNull() ?: value.get()
    }

    @JvmSynthetic
    fun orElse(value: () -> T): T {
        return orNull() ?: value()
    }

    fun orNullableDefault(value: T?): T? {
        return orNull() ?: value
    }

    fun orNullableElse(value: Supplier<T?>): T? {
        return orNull() ?: value.get()
    }

    @JvmSynthetic
    fun orNullableElse(value: () -> T?): T? {
        return orNull() ?: value()
    }
}

interface SetRef<T : Any> {

    fun set(value: T?)
}

/**
 * Boolean version of [Ref].
 */
@ForJava
interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean): BooleanRef

    fun copy(): BooleanRef {
        return get().intRef()
    }

    fun toOptional(): OptionalInt {
        return OptionalInt.of(get().toInt())
    }

    fun toRef(): Ref<Boolean> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Boolean.intRef(): BooleanRef {
            return BooleanRefImpl(this)
        }

        private class BooleanRefImpl(private var value: Boolean) : BooleanRef {
            override fun get(): Boolean = value

            override fun set(value: Boolean) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Byte version of [Ref].
 */
@ForJava
interface ByteRef {

    fun get(): Byte

    fun set(value: Byte): ByteRef

    fun copy(): ByteRef {
        return get().intRef()
    }

    fun toOptional(): OptionalInt {
        return OptionalInt.of(get().toUnsignedInt())
    }

    fun toRef(): Ref<Byte> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Byte.intRef(): ByteRef {
            return ByteRefImpl(this)
        }

        private class ByteRefImpl(private var value: Byte) : ByteRef {
            override fun get(): Byte = value

            override fun set(value: Byte) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Short version of [Ref].
 */
@ForJava
interface ShortRef {

    fun get(): Short

    fun set(value: Short): ShortRef

    fun copy(): ShortRef {
        return get().intRef()
    }

    fun toOptional(): OptionalInt {
        return OptionalInt.of(get().toUnsignedInt())
    }

    fun toRef(): Ref<Short> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Short.intRef(): ShortRef {
            return ShortRefImpl(this)
        }

        private class ShortRefImpl(private var value: Short) : ShortRef {
            override fun get(): Short = value

            override fun set(value: Short) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Char version of [Ref].
 */
@ForJava
interface CharRef {

    fun get(): Char

    fun set(value: Char): CharRef

    fun copy(): CharRef {
        return get().intRef()
    }

    fun toOptional(): OptionalInt {
        return OptionalInt.of(get().code)
    }

    fun toRef(): Ref<Char> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Char.intRef(): CharRef {
            return CharRefImpl(this)
        }

        private class CharRefImpl(private var value: Char) : CharRef {
            override fun get(): Char = value

            override fun set(value: Char) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Int version of [Ref].
 */
@ForJava
interface IntRef {

    fun get(): Int

    fun set(value: Int): IntRef

    fun copy(): IntRef {
        return get().intRef()
    }

    fun toOptional(): OptionalInt {
        return OptionalInt.of(get())
    }

    fun toRef(): Ref<Int> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Int.intRef(): IntRef {
            return IntRefImpl(this)
        }

        private class IntRefImpl(private var value: Int) : IntRef {
            override fun get(): Int = value

            override fun set(value: Int) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Long version of [Ref].
 */
@ForJava
interface LongRef {

    fun get(): Long

    fun set(value: Long): LongRef

    fun copy(): LongRef {
        return get().intRef()
    }

    fun toOptional(): OptionalLong {
        return OptionalLong.of(get())
    }

    fun toRef(): Ref<Long> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Long.intRef(): LongRef {
            return LongRefImpl(this)
        }

        private class LongRefImpl(private var value: Long) : LongRef {
            override fun get(): Long = value

            override fun set(value: Long) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Float version of [Ref].
 */
@ForJava
interface FloatRef {

    fun get(): Float

    fun set(value: Float): FloatRef

    fun copy(): FloatRef {
        return get().intRef()
    }

    fun toOptional(): OptionalDouble {
        return OptionalDouble.of(get().toDouble())
    }

    fun toRef(): Ref<Float> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Float.intRef(): FloatRef {
            return FloatRefImpl(this)
        }

        private class FloatRefImpl(private var value: Float) : FloatRef {
            override fun get(): Float = value

            override fun set(value: Float) = apply {
                this.value = value
            }
        }
    }
}

/**
 * Double version of [Ref].
 */
@ForJava
interface DoubleRef {

    fun get(): Double

    fun set(value: Double): DoubleRef

    fun copy(): DoubleRef {
        return get().intRef()
    }

    fun toOptional(): OptionalDouble {
        return OptionalDouble.of(get())
    }

    fun toRef(): Ref<Double> {
        return get().ref()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Double.intRef(): DoubleRef {
            return DoubleRefImpl(this)
        }

        private class DoubleRefImpl(private var value: Double) : DoubleRef {
            override fun get(): Double = value

            override fun set(value: Double) = apply {
                this.value = value
            }
        }
    }
}
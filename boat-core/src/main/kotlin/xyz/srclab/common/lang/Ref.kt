package xyz.srclab.common.lang

import xyz.srclab.common.lang.Ref.Companion.toRef
import java.util.function.Consumer

/**
 * A reference holds an object.
 *
 * It supports chain operation in Java:
 *
 * ```
 * Date dateValue = Ref.of(intValue)
 *     .apply(StringUtils::intToString)
 *     .accept(SystemUtils::printString)
 *     .apply(DateUtils::stringToDate)
 *     .get();
 * ```
 *
 * Same as:
 *
 * ```
 * String stringValue = StringUtils.intToString(intValue);
 * SystemUtils.printString(stringValue);
 * Date dateValue = DateUtils.stringToDate(stringValue);
 * ```
 *
 * It also provides a mutable place for final variable such as local variable in Java lambda expression:
 *
 * ```
 * Ref<String> ref = Ref.of("1");
 * List<String> list = Arrays.asList("-1", "-2", "-3");
 *
 * //here <String> should be final without Ref
 * list.forEach(i -> ref.set(ref.get() + i));
 * ```
 *
 * @see BooleanRef
 * @see ByteRef
 * @see ShortRef
 * @see CharRef
 * @see IntRef
 * @see LongRef
 * @see FloatRef
 * @see DoubleRef
 */
interface Ref<T : Any> : GenericAccessor<T> {

    /**
     * If value of this [Ref] is `non-null`, given [action] will be executed, else not.
     *
     * Returns [Ref] of result of execution, or [Ref] of `null` if not be executed.
     */
    fun <R : Any> apply(action: (T) -> R): Ref<R> {
        val v = getOrNull()
        return if (v === null) {
            null.toRef()
        } else {
            action(v).toRef()
        }
    }

    /**
     * Executes given [action], returns [Ref] of result of execution.
     */
    fun <R : Any> applyOrNull(action: (T?) -> R?): Ref<R> {
        return action(getOrNull()).toRef()
    }

    /**
     * If value of this [Ref] is `non-null`, given [action] will be executed, else not.
     *
     * Returns this.
     */
    fun accept(action: Consumer<T>): Ref<T> {
        val v = getOrNull()
        if (v !== null) {
            action.accept(v)
        }
        return this
    }

    /**
     * Executes given [action], returns this.
     */
    fun acceptOrNull(action: Consumer<T?>): Ref<T> {
        action.accept(getOrNull())
        return this
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.toRef(): Ref<T> {
            return RefImpl(this)
        }

        private class RefImpl<T : Any>(
            private var value: T?
        ) : Ref<T> {

            override fun getOrNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}

/**
 * Boolean type of [Ref].
 */
interface BooleanRef {

    fun get(): Boolean

    fun set(value: Boolean)

    fun toObjectRef(): Ref<Boolean> {
        return get().toRef<Boolean>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Boolean.toRef(): BooleanRef {
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

/**
 * Byte type of [Ref].
 */
interface ByteRef {

    fun get(): Byte

    fun set(value: Byte)

    fun toObjectRef(): Ref<Byte> {
        return get().toRef<Byte>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Byte.toRef(): ByteRef {
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

/**
 * Short type of [Ref].
 */
interface ShortRef {

    fun get(): Short

    fun set(value: Short)

    fun toObjectRef(): Ref<Short> {
        return get().toRef<Short>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Short.toRef(): ShortRef {
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

/**
 * Char type of [Ref].
 */
interface CharRef {

    fun get(): Char

    fun set(value: Char)

    fun toObjectRef(): Ref<Char> {
        return get().toRef<Char>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Char.toRef(): CharRef {
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

/**
 * Int type of [Ref].
 */
interface IntRef {

    fun get(): Int

    fun set(value: Int)

    fun toObjectRef(): Ref<Int> {
        return get().toRef<Int>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Int.toRef(): IntRef {
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

/**
 * Long type of [Ref].
 */
interface LongRef {

    fun get(): Long

    fun set(value: Long)

    fun toObjectRef(): Ref<Long> {
        return get().toRef<Long>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Long.toRef(): LongRef {
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

/**
 * Float type of [Ref].
 */
interface FloatRef {

    fun get(): Float

    fun set(value: Float)

    fun toObjectRef(): Ref<Float> {
        return get().toRef<Float>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Float.toRef(): FloatRef {
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

/**
 * Double type of [Ref].
 */
interface DoubleRef {

    fun get(): Double

    fun set(value: Double)

    fun toObjectRef(): Ref<Double> {
        return get().toRef<Double>()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Double.toRef(): DoubleRef {
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
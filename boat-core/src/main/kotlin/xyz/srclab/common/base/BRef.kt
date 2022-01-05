@file:JvmName("BRef")

package xyz.srclab.common.base

import xyz.srclab.annotations.ForJava
import xyz.srclab.common.base.Ref.Companion.referred
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

fun <T : Any> Optional<T>.toRef(): Ref<T> {
    return this.orElse(null).referred()
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
        return orNull().referred()
    }

    fun toOptional(): Optional<T> {
        return Optional.ofNullable(orNull())
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T : Any> T?.referred(): Ref<T> {
            return BRefImpl(this)
        }

        private class BRefImpl<T : Any>(private var value: T?) : Ref<T> {

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
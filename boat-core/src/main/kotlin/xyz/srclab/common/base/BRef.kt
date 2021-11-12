package xyz.srclab.common.base

import xyz.srclab.annotations.ForJava
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
 * It also provides a mutable reference for final variable:
 *
 * ```
 * Ref<String> ref = Ref.of("1");
 * List<String> list = Arrays.asList("-1", "-2", "-3");
 * list.forEach(i -> ref.set(ref.get() + i));
 * ```
 */
@ForJava
interface BRef<T : Any> : BAccessor<T> {

    /**
     * If current value is `non-null`, given [action] will be executed, returns `this` with new value.
     * If current value is `null`, returns `this` with `null` value.
     */
    fun <R : Any> apply(action: (T) -> R): BRef<R> {
        val cur = getOrNull()
        if (cur === null) {
            return this.asTyped()
        }
        val thisRef = this.asTyped<BRef<R>>()
        thisRef.set(action(cur))
        return thisRef
    }

    /**
     * Given [action] will be executed, returns `this` with new value.
     */
    fun <R : Any> applyOrNull(action: (T?) -> R): BRef<R> {
        val thisRef = this.asTyped<BRef<R>>()
        thisRef.set(action(getOrNull()))
        return thisRef
    }

    /**
     * If current value is `non-null`, given [action] will be executed, returns `this`.
     */
    fun accept(action: Consumer<T>): BRef<T> {
        val cur = getOrNull()
        if (cur === null) {
            return this.asTyped()
        }
        action.accept(cur)
        return this
    }

    /**
     * Given [action] will be executed, returns `this`.
     */
    fun acceptOrNull(action: Consumer<T?>): BRef<T> {
        action.accept(getOrNull())
        return this
    }

    companion object {

        @JvmStatic
        fun <T : Any> of(obj: T?): BRef<T> {
            return BRefImpl(obj)
        }

        private class BRefImpl<T : Any>(private var value: T?) : BRef<T> {

            override fun getOrNull(): T? {
                return value
            }

            override fun set(value: T?) {
                this.value = value
            }
        }
    }
}
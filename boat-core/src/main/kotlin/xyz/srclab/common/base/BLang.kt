@file:JvmName("BLang")

package xyz.srclab.common.base

import xyz.srclab.common.base.JumpState.*
import java.util.concurrent.Callable
import java.util.function.*
import java.util.function.Function

fun <T> Supplier<T>.toKotlinFun(): (() -> T) = {
    this.get()
}

fun <T> Predicate<T>.toKotlinFun(): (T) -> Boolean = {
    this.test(it)
}

fun <T, R> Function<T, R>.toKotlinFun(): (T) -> R = {
    this.apply(it)
}

fun <R> IntFunction<R>.toKotlinFun(): (Int) -> R = {
    this.apply(it)
}

fun <T> Consumer<T>.toKotlinFun(): (T) -> Unit = {
    this.accept(it)
}

fun <T, U> BiPredicate<T, U>.toKotlinFun(): (T, U) -> Boolean = { it0, it1 ->
    this.test(it0, it1)
}

fun <T, U, R> BiFunction<T, U, R>.toKotlinFun(): (T, U) -> R = { it0, it1 ->
    this.apply(it0, it1)
}

fun <T, U> BiConsumer<T, U>.toKotlinFun(): (T, U) -> Unit = { it0, it1 ->
    this.accept(it0, it1)
}

fun <T> IndexPredicate<T>.toKotlinFun(): (Int, T) -> Boolean = { i, it ->
    this.test(i, it)
}

fun <T, R> IndexFunction<T, R>.toKotlinFun(): (Int, T) -> R = { i, it ->
    this.apply(i, it)
}

fun <T, U, R> IndexBiFunction<T, U, R>.toKotlinFun(): (Int, T, U) -> R = { i, it0, it1 ->
    this.apply(i, it0, it1)
}

fun <T> IndexConsumer<T>.toKotlinFun(): (Int, T) -> Unit = { i, it ->
    this.accept(i, it)
}

fun <T> (() -> T).toSupplier(): Supplier<T> {
    return Supplier { this() }
}

fun <T> ((T) -> Boolean).toPredicate(): Predicate<T> {
    return Predicate { this(it) }
}

fun <T, R> ((T) -> R).toFunction(): Function<T, R> {
    return Function { this(it) }
}

fun <R> ((Int) -> R).toIntFunction(): IntFunction<R> {
    return IntFunction { this(it) }
}

fun <T> ((T) -> Any?).toConsumer(): Consumer<T> {
    return Consumer { this(it) }
}

fun <T, U> ((T, U) -> Boolean).toPredicate(): BiPredicate<T, U> {
    return BiPredicate { it0, it1 -> this(it0, it1) }
}

fun <T, U, R> ((T, U) -> R).toFunction(): BiFunction<T, U, R> {
    return BiFunction { it0, it1 -> this(it0, it1) }
}

fun <T, U> ((T, U) -> Any?).toConsumer(): BiConsumer<T, U> {
    return BiConsumer { it0, it1 -> this(it0, it1) }
}

fun <R> (() -> R).toRunnable(): Runnable {
    return Runnable { this() }
}

fun <R> (() -> R).toCallable(): Callable<R> {
    return Callable { this() }
}

fun <T> ((Int, T) -> Boolean).toIndexPredicate(): IndexPredicate<T> {
    return object : IndexPredicate<T> {
        override fun test(index: Int, t: T): Boolean {
            return this@toIndexPredicate(index, t)
        }
    }
}

fun <T, R> ((Int, T) -> R).toIndexFunction(): IndexFunction<T, R> {
    return object : IndexFunction<T, R> {
        override fun apply(index: Int, t: T): R {
            return this@toIndexFunction(index, t)
        }
    }
}

fun <T, U, R> ((Int, T, U) -> R).toIndexFunction(): IndexBiFunction<T, U, R> {
    return object : IndexBiFunction<T, U, R> {
        override fun apply(index: Int, t: T, u: U): R {
            return this@toIndexFunction(index, t, u)
        }
    }
}

fun <T> ((Int, T) -> Any?).toIndexConsumer(): IndexConsumer<T> {
    return object : IndexConsumer<T> {
        override fun accept(index: Int, t: T) {
            this@toIndexConsumer(index, t)
        }
    }
}

/**
 * Jump statement for process control: [CONTINUE], [BREAK] or [RETURN].
 */
enum class JumpState {

    /**
     * Stops the current execution of the iteration and proceeds to the next iteration in the loop.
     */
    CONTINUE,

    /**
     * Stops loop.
     */
    BREAK,

    /**
     * Stops the current execution of the method and returns.
     */
    RETURN,
    ;

    fun isContinue(): Boolean {
        return this == CONTINUE
    }

    fun isBreak(): Boolean {
        return this == BREAK
    }

    fun isReturn(): Boolean {
        return this == RETURN
    }
}

/**
 * Policy of thread-safe.
 */
enum class ThreadSafePolicy {

    /**
     * None thread-safe.
     */
    NONE,

    /**
     * Synchronized.
     */
    SYNCHRONIZED,

    /**
     * Concurrent.
     */
    CONCURRENT,

    /**
     * Thread-local.
     */
    THREAD_LOCAL,

    /**
     * Copy-on-write.
     */
    COPY_ON_WRITE,
}

@FunctionalInterface
interface IndexPredicate<T> {

    fun test(index: Int, t: T): Boolean
}

@FunctionalInterface
interface IndexFunction<T, R> {

    fun apply(index: Int, t: T): R
}

@FunctionalInterface
interface IndexBiFunction<T, U, R> {

    fun apply(index: Int, t: T, u: U): R
}

@FunctionalInterface
interface IndexConsumer<T> {

    fun accept(index: Int, t: T)
}
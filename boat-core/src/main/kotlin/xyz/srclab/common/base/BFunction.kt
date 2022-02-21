@file:JvmName("BFunction")

package xyz.srclab.common.base

import java.util.concurrent.Callable
import java.util.function.*
import java.util.function.Function

@FunctionalInterface
interface IndexedPredicate<T> {
    fun test(index: Int, t: T): Boolean
}

@FunctionalInterface
interface IndexedFunction<T, R> {
    fun apply(index: Int, t: T): R
}

@FunctionalInterface
interface IndexedBiFunction<T, U, R> {
    fun apply(index: Int, t: T, u: U): R
}

@FunctionalInterface
interface IndexedConsumer<T> {
    fun accept(index: Int, t: T)
}

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

fun <T> IndexedPredicate<T>.toKotlinFun(): (Int, T) -> Boolean = { i, it ->
    this.test(i, it)
}

fun <T, R> IndexedFunction<T, R>.toKotlinFun(): (Int, T) -> R = { i, it ->
    this.apply(i, it)
}

fun <T, U, R> IndexedBiFunction<T, U, R>.toKotlinFun(): (Int, T, U) -> R = { i, it0, it1 ->
    this.apply(i, it0, it1)
}

fun <T> IndexedConsumer<T>.toKotlinFun(): (Int, T) -> Unit = { i, it ->
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

fun <T> ((Int, T) -> Boolean).toIndexPredicate(): IndexedPredicate<T> {
    return object : IndexedPredicate<T> {
        override fun test(index: Int, t: T): Boolean {
            return this@toIndexPredicate(index, t)
        }
    }
}

fun <T, R> ((Int, T) -> R).toIndexFunction(): IndexedFunction<T, R> {
    return object : IndexedFunction<T, R> {
        override fun apply(index: Int, t: T): R {
            return this@toIndexFunction(index, t)
        }
    }
}

fun <T, U, R> ((Int, T, U) -> R).toIndexFunction(): IndexedBiFunction<T, U, R> {
    return object : IndexedBiFunction<T, U, R> {
        override fun apply(index: Int, t: T, u: U): R {
            return this@toIndexFunction(index, t, u)
        }
    }
}

fun <T> ((Int, T) -> Any?).toIndexConsumer(): IndexedConsumer<T> {
    return object : IndexedConsumer<T> {
        override fun accept(index: Int, t: T) {
            this@toIndexConsumer(index, t)
        }
    }
}
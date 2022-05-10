@file: JvmName("BKotlin")

package xyz.srclab.common.base

import java.util.concurrent.Callable
import java.util.function.*

// Java functional interfaces to kotlin function:

fun <T> Supplier<T>.asKotlinFun(): (() -> T) = { this.get() }
fun IntSupplier.asKotlinFun(): (() -> Int) = { this.asInt }
fun LongSupplier.asKotlinFun(): (() -> Long) = { this.asLong }
fun DoubleSupplier.asKotlinFun(): (() -> Double) = { this.asDouble }

fun <T> Predicate<T>.asKotlinFun(): (T) -> Boolean = { this.test(it) }
fun <T, R> JavaFunction<T, R>.asKotlinFun(): (T) -> R = { this.apply(it) }
fun <T> Consumer<T>.asKotlinFun(): (T) -> Unit = { this.accept(it) }

fun <T, U> BiPredicate<T, U>.asKotlinFun(): (T, U) -> Boolean = { t, u -> this.test(t, u) }
fun <T, U, R> BiFunction<T, U, R>.asKotlinFun(): (T, U) -> R = { t, u -> this.apply(t, u) }
fun <T, U> BiConsumer<T, U>.asKotlinFun(): (T, U) -> Unit = { t, u -> this.accept(t, u) }

fun <R> IntFunction<R>.asKotlinFun(): (Int) -> R = { this.apply(it) }
fun IntToLongFunction.asKotlinFun(): (Int) -> Long = { this.applyAsLong(it) }
fun IntToDoubleFunction.asKotlinFun(): (Int) -> Double = { this.applyAsDouble(it) }
fun <R> LongFunction<R>.asKotlinFun(): (Long) -> R = { this.apply(it) }
fun LongToIntFunction.asKotlinFun(): (Long) -> Int = { this.applyAsInt(it) }
fun LongToDoubleFunction.asKotlinFun(): (Long) -> Double = { this.applyAsDouble(it) }
fun <R> DoubleFunction<R>.asKotlinFun(): (Double) -> R = { this.apply(it) }
fun DoubleToIntFunction.asKotlinFun(): (Double) -> Int = { this.applyAsInt(it) }
fun DoubleToLongFunction.asKotlinFun(): (Double) -> Long = { this.applyAsLong(it) }

fun <T> IndexedPredicate<T>.asKotlinFun(): (Int, T) -> Boolean = { i, t -> this.test(i, t) }
fun <T, R> IndexedFunction<T, R>.asKotlinFun(): (Int, T) -> R = { i, t -> this.apply(i, t) }
fun <T> IndexedConsumer<T>.asKotlinFun(): (Int, T) -> Unit = { i, t -> this.accept(i, t) }
fun <T, U> IndexedBiPredicate<T, U>.asKotlinFun(): (Int, T, U) -> Boolean = { i, t, u -> this.test(i, t, u) }
fun <T, U, R> IndexedBiFunction<T, U, R>.asKotlinFun(): (Int, T, U) -> R = { i, t, u -> this.apply(i, t, u) }
fun <T, U> IndexedBiConsumer<T, U>.asKotlinFun(): (Int, T, U) -> Unit = { i, t, u -> this.accept(i, t, u) }

fun Runnable.asKotlinFun(): () -> Unit = { this.run() }
fun <V> Callable<V>.asKotlinFun(): () -> V = { this.call() }

// Kotlin functions to java functional interfaces:

private typealias IBiPredicate<T, U> = IndexedBiPredicate<T, U>
private typealias IBiFunction<T, U, R> = IndexedBiFunction<T, U, R>
private typealias IBiConsumer<T, U> = IndexedBiConsumer<T, U>

fun <T> (() -> T).asJavaFun(): Supplier<T> = Supplier { this() }
fun <T> (() -> Int).asJavaFun(): IntSupplier = IntSupplier { this() }
fun <T> (() -> Long).asJavaFun(): LongSupplier = LongSupplier { this() }
fun <T> (() -> Double).asJavaFun(): DoubleSupplier = DoubleSupplier { this() }

fun <T> ((T) -> Boolean).asJavaFun(): Predicate<T> = Predicate { this(it) }
fun <T, R> ((T) -> R).asJavaFun(): JavaFunction<T, R> = Function { this(it) }
fun <T> ((T) -> Unit).asJavaFun(): Consumer<T> = Consumer { this(it) }

fun <T, U> ((T, U) -> Boolean).asJavaFun(): BiPredicate<T, U> = BiPredicate { t, u -> this(t, u) }
fun <T, U, R> ((T, U) -> R).asJavaFun(): BiFunction<T, U, R> = BiFunction { t, u -> this(t, u) }
fun <T, U> ((T, U) -> Unit).asJavaFun(): BiConsumer<T, U> = BiConsumer { t, u -> this(t, u) }

fun <R> ((Int) -> R).asJavaFun(): IntFunction<R> = IntFunction { this(it) }
fun ((Int) -> Long).asJavaFun(): IntToLongFunction = IntToLongFunction { this(it) }
fun ((Int) -> Double).asJavaFun(): IntToDoubleFunction = IntToDoubleFunction { this(it) }
fun <R> ((Long) -> R).asJavaFun(): LongFunction<R> = LongFunction { this(it) }
fun ((Long) -> Int).asJavaFun(): LongToIntFunction = LongToIntFunction { this(it) }
fun ((Long) -> Double).asJavaFun(): LongToDoubleFunction = LongToDoubleFunction { this(it) }
fun <R> ((Double) -> R).asJavaFun(): DoubleFunction<R> = DoubleFunction { this(it) }
fun ((Double) -> Int).asJavaFun(): DoubleToIntFunction = DoubleToIntFunction { this(it) }
fun ((Double) -> Long).asJavaFun(): DoubleToLongFunction = DoubleToLongFunction { this(it) }

fun <T> ((Int, T) -> Boolean).asJavaFun(): IndexedPredicate<T> = IndexedPredicate { i, it -> this(i, it) }
fun <T, R> ((Int, T) -> R).asJavaFun(): IndexedFunction<T, R> = IndexedFunction { i, it -> this(i, it) }
fun <T> ((Int, T) -> Unit).asJavaFun(): IndexedConsumer<T> = IndexedConsumer { i, t -> this(i, t) }
fun <T, U> ((Int, T, U) -> Boolean).asJavaFun(): IndexedBiPredicate<T, U> = IBiPredicate { i, t, u -> this(i, t, u) }
fun <T, U, R> ((Int, T, U) -> R).asJavaFun(): IndexedBiFunction<T, U, R> = IBiFunction { i, t, u -> this(i, t, u) }
fun <T, U> ((Int, T, U) -> Unit).asJavaFun(): IndexedBiConsumer<T, U> = IBiConsumer { i, t, u -> this(i, t, u) }

fun (() -> Any?).asRunnable(): Runnable = Runnable { this() }
fun <R> (() -> R).asCallable(): Callable<R> = Callable { this() }

// Java types:

/**
 * Alias for [java.lang.String].
 */
typealias JavaString = java.lang.String
/**
 * Alias for [java.lang.Boolean].
 */
typealias JavaBoolean = java.lang.Boolean
/**
 * Alias for [java.lang.Byte].
 */
typealias JavaByte = java.lang.Byte
/**
 * Alias for [java.lang.Short].
 */
typealias JavaShort = java.lang.Short
/**
 * Alias for [java.lang.Character].
 */
typealias JavaChar = java.lang.Character
/**
 * Alias for [java.lang.Integer].
 */
typealias JavaInt = java.lang.Integer
/**
 * Alias for [java.lang.Long].
 */
typealias JavaLong = java.lang.Long
/**
 * Alias for [java.lang.Float].
 */
typealias JavaFloat = java.lang.Float
/**
 * Alias for [java.lang.Double].
 */
typealias JavaDouble = java.lang.Double
/**
 * Alias for [java.lang.Void].
 */
typealias JavaVoid = java.lang.Void
/**
 * Alias for [java.lang.Enum].
 */
typealias JavaEnum<T> = java.lang.Enum<T>
/**
 * Alias for [java.util.function.Function].
 */
typealias JavaFunction<T, R> = java.util.function.Function<T, R>
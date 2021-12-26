@file:JvmName("BLangs")

package xyz.srclab.common.base

import xyz.srclab.common.base.JumpState.*
import java.util.function.*
import java.util.function.Function

fun <T> Predicate<T>.toFunction(): (T) -> Boolean = {
    this.test(it)
}

fun <T, R> Function<T, R>.toFunction(): (T) -> R = {
    this.apply(it)
}

fun <T> Consumer<T>.toFunction(): (T) -> Unit = {
    this.accept(it)
}

fun <T, U> BiPredicate<T, U>.toFunction(): (T, U) -> Boolean = { it1, it2 ->
    this.test(it1, it2)
}

fun <T, U, R> BiFunction<T, U, R>.toFunction(): (T, U) -> R = { it1, it2 ->
    this.apply(it1, it2)
}

fun <T, U> BiConsumer<T, U>.toFunction(): (T, U) -> Unit = { it1, it2 ->
    this.accept(it1, it2)
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
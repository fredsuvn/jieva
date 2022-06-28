@file:JvmName("LangBt")

package xyz.srclab.common.base

/**
 * Gets or creates a new value.
 * This function is usually used for the pattern:
 *
 * Checks whether the value is null, if it is not, return, else create a new one and set and return.
 */
inline fun <T> getOrNew(
    lock: Any,
    getter: () -> T?,
    setter: (T) -> Unit,
    creator: () -> T
): T {
    val v = getter()
    if (v !== null) {
        return v
    }
    synchronized(lock) {
        val v0 = getter()
        if (v0 !== null) {
            return v0
        }
        val nv = creator()
        setter(nv)
        return nv
    }
}

/**
 * Abstract class represents a final class, which will cache the values of [hashCode] and [toString].
 * The subclass should implement [hashCode0] and [toString0] to compute the values of [hashCode] and [toString],
 * each computation will be processed only once.
 */
abstract class FinalClass {

    private var _hashCode: Int? = null
    private var _toString: String? = null

    override fun hashCode(): Int {
        return getOrNew(
            this,
            { this._hashCode },
            { this._hashCode = it },
            { hashCode0() },
        )
    }

    override fun toString(): String {
        return getOrNew(
            this,
            { this._toString },
            { this._toString = it },
            { toString0() },
        )
    }

    /**
     * Computes the hash code.
     */
    protected abstract fun hashCode0(): Int

    /**
     * Computes the toString value.
     */
    protected abstract fun toString0(): String
}

/*
 * --------------------------------------------------------------------------------
 *  Extension Java functional interfaces start:
 * --------------------------------------------------------------------------------
 */

/**
 * Functional interface represents [java.util.function.Predicate] with index.
 */
fun interface IndexedPredicate<T> {
    fun test(index: Int, t: T): Boolean
}

/**
 * Functional interface represents [java.util.function.Function] with index.
 */
fun interface IndexedFunction<T, R> {
    fun apply(index: Int, t: T): R
}

/**
 * Functional interface represents [java.util.function.Consumer] with index.
 */
fun interface IndexedConsumer<T> {
    fun accept(index: Int, t: T)
}

/**
 * Functional interface represents [java.util.function.BiPredicate] with index.
 */
fun interface IndexedBiPredicate<T, U> {
    fun test(index: Int, t: T, u: U): Boolean
}

/**
 * Functional interface represents [java.util.function.BiFunction] with index.
 */
fun interface IndexedBiFunction<T, U, R> {
    fun apply(index: Int, t: T, u: U): R
}

/**
 * Functional interface represents [java.util.function.BiConsumer] with index.
 */
fun interface IndexedBiConsumer<T, U> {
    fun accept(index: Int, t: T, u: U)
}

/*
 * --------------------------------------------------------------------------------
 *  Extension Java functional interfaces end:
 * --------------------------------------------------------------------------------
 */

/*
 * --------------------------------------------------------------------------------
 *  Policies start:
 * --------------------------------------------------------------------------------
 */

/**
 * Policy of jump statement for process control: [CONTINUE], [BREAK] and [RETURN].
 */
enum class JumpPolicy {

    /**
     * Stops the current execution of the iteration and proceeds to the next iteration in the loop.
     */
    CONTINUE,

    /**
     * Stops the current loop and breaks out.
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
 * Policy for thread-safe.
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

/*
 * --------------------------------------------------------------------------------
 *  Policies end.
 * --------------------------------------------------------------------------------
 */
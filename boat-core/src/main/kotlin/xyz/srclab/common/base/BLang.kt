package xyz.srclab.common.base

import xyz.srclab.common.base.BJumpState.*
import xyz.srclab.common.base.ThreadSafePolicy.*

/**
 * Jump statement for process control: [CONTINUE], [BREAK] or [RETURN].
 */
enum class BJumpState {

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
 * Used to specify policy of thread-safe: [NONE], [SYNCHRONIZED] or [THREAD_LOCAL].
 */
enum class ThreadSafePolicy {

    /**
     * None thread-safe policy.
     */
    NONE,

    /**
     * Synchronization policy.
     */
    SYNCHRONIZED,

    /**
     * Thread-local policy.
     */
    THREAD_LOCAL
}
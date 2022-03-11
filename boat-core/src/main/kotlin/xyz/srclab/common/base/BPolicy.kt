package xyz.srclab.common.base

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
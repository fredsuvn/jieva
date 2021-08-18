package xyz.srclab.common.invoke

/**
 * Represents a invoke.
 */
interface Invoke {

    /**
     * Starts this [Invoke].
     */
    fun <T> start(vararg args: Any?): T {
        return startWith(false, *args)
    }

    /**
     *  Starts this [Invoke] with [force].
     */
    fun <T> startWith(force: Boolean, vararg args: Any?): T
}
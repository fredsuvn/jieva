package xyz.srclab.common.invoke

/**
 * Represents prepared invoking.
 */
interface Invoke {

    /**
     * Starts invoking.
     */
    fun <T> start(vararg args: Any?): T
}
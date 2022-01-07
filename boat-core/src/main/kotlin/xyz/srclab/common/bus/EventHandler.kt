package xyz.srclab.common.bus

/**
 * To deal with event of [T].
 */
interface EventHandler<T> {

    val eventType: Class<T>

    /**
     * Handles given [event].
     */
    fun doEvent(event: T)
}
package xyz.srclab.common.bus

/**
 * To deal with event of [T].
 *
 * @see EventBus
 */
interface EventBusHandler<T> {

    val eventType: Class<T>

    /**
     * Handles given [event].
     */
    fun doEvent(event: T)
}
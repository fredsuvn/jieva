package xyz.srclab.common.base

import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.Consumer

/**
 * Event bus to listen and emit events for mapped channels.
 */
interface EventBus {

    /**
     * Adds and replaces listener on [channel].
     *
     * @param channel event type
     * @param handler event handler
     */
    fun <T> on(channel: Any, handler: Consumer<in T>)

    /**
     * Removes listener on [channel].
     */
    fun off(channel: Any)

    /**
     * Emits event on [channel].
     */
    fun emit(channel: Any, event: Any)

    companion object {

        /**
         * Returns new [EventBus].
         *
         * @param executor executor where event handlers run on
         * @param handlerMap [MutableMap] to store the mapping of event channels and handlers.
         */
        @JvmStatic
        @JvmOverloads
        fun newEventBus(
            executor: Executor = SyncRunner,
            handlerMap: MutableMap<Any, Consumer<*>> = ConcurrentHashMap()
        ): EventBus {
            return EventBusImpl(executor, handlerMap)
        }

        private class EventBusImpl(
            private val executor: Executor,
            private val handlerMap: MutableMap<Any, Consumer<*>>,
        ) : EventBus {

            override fun <T> on(channel: Any, handler: Consumer<in T>) {
                handlerMap[channel] = handler
            }

            override fun off(channel: Any) {
                handlerMap.remove(channel)
            }

            override fun emit(channel: Any, event: Any) {
                val handler: Consumer<Any>? = handlerMap[channel].asType()
                if (handler === null) {
                    return
                }
                executor.execute { handler.accept(event) }
            }
        }
    }
}
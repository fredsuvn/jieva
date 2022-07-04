package xyz.srclab.common.base

import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Event bus to listen and emit events for mapped channels.
 */
interface EventBus {

    /**
     * Adds and replaces listener on [channel].
     *
     * @param T type of event
     * @param channel event type
     * @param handler event handler
     */
    fun <T> on(channel: Any, handler: Consumer<in T>)

    /**
     * Adds and replaces listener on [channel].
     *
     * This method can share the context through current thread and execution thread of [handler].
     *
     *@param T type of event
     * @param C type of context
     * @param channel event type
     * @param handler event handler
     */
    fun <C, T> on(channel: Any, handler: BiConsumer<in T, in C>)

    /**
     * Removes listener on [channel].
     */
    fun off(channel: Any)

    /**
     * Emits [event] on [channel].
     */
    fun emit(channel: Any, event: Any)

    /**
     * Emits [event] and [context] on [channel].
     */
    fun <C> emit(channel: Any, event: Any, context: C)

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
            handlerMap: MutableMap<Any, Any> = ConcurrentHashMap()
        ): EventBus {
            return EventBusImpl(executor, handlerMap)
        }

        private class EventBusImpl(
            private val executor: Executor,
            private val handlerMap: MutableMap<Any, Any>,
        ) : EventBus {

            override fun <T> on(channel: Any, handler: Consumer<in T>) {
                handlerMap[channel] = handler
            }

            override fun <C, T> on(channel: Any, handler: BiConsumer<in T, in C>) {
                handlerMap[channel] = handler
            }

            override fun off(channel: Any) {
                handlerMap.remove(channel)
            }

            override fun emit(channel: Any, event: Any) {
                val handler = handlerMap[channel]
                if (handler === null || handler !is Consumer<*>) {
                    return
                }
                executor.execute { handler.asType<Consumer<Any?>>().accept(event) }
            }

            override fun <C> emit(channel: Any, event: Any, context: C) {
                val handler = handlerMap[channel]
                if (handler === null || handler !is BiConsumer<*, *>) {
                    return
                }
                executor.execute { handler.asType<BiConsumer<Any?, C>>().accept(event, context) }
            }
        }
    }
}
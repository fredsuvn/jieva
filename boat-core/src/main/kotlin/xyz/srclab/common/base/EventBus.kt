package xyz.srclab.common.base

import xyz.srclab.common.base.EventBus.Handler
import xyz.srclab.common.collect.arrayAdd
import xyz.srclab.common.collect.arrayRemove
import xyz.srclab.common.reflect.canAssignedBy
import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.Executor

/**
 * Event bus. Use [register] or [registerAll] to register event handler, [unregister] and [unregisterAll] to unregister.
 *
 * [Handler] is used to handle event.
 * [EventBus] will post event for each [Handler] of which [Handler.eventType] is assignable from event,
 * in sub-to-super order.
 */
interface EventBus {

    /**
     * Registers [handler].
     */
    fun register(handler: Handler<*>)

    /**
     * Registers [handlers].
     */
    fun registerAll(handlers: Iterable<Handler<*>>) {
        for (handler in handlers) {
            register(handler)
        }
    }

    /**
     * Unregisters [handler].
     */
    fun unregister(handler: Handler<*>)

    /**
     * Unregisters [handlers].
     */
    fun unregisterAll(handlers: Iterable<Handler<*>>) {
        for (handler in handlers) {
            unregister(handler)
        }
    }

    /**
     * Posts event for registered handlers.
     */
    fun post(event: Any)

    /**
     * To deal with event of [T].
     */
    interface Handler<T> {

        val eventType: Class<T>

        /**
         * Handles given [event].
         */
        fun doEvent(event: T)
    }

    companion object {

        /**
         * Returns new [EventBus].
         *
         * @param executor executor where event handlers run on
         */
        @JvmStatic
        @JvmOverloads
        fun newEventBus(executor: Executor = SyncRunner): EventBus {
            return EventBusImpl(executor)
        }

        private class EventBusImpl(
            private val executor: Executor
        ) : EventBus {

            private var handlers: Array<Handler<*>> = emptyArray()

            @Synchronized
            override fun register(handler: Handler<*>) {
                var i = 0
                while (i < handlers.size) {
                    val hi = handlers[i]
                    if (hi == handler) {
                        handlers[i] = handler
                        return
                    }
                    val ci = hi.eventType
                    val c = handler.eventType
                    if (ci == c) {
                        handlers = handlers.arrayAdd(handler, i)
                        return
                    }
                    if (ci.canAssignedBy(c)) {
                        handlers = handlers.arrayAdd(handler, i)
                        return
                    }
                    if (c.canAssignedBy(ci)) {
                        i++
                        while (i < handlers.size) {
                            val hx = handlers[i]
                            if (hx == handler) {
                                handlers[i] = handler
                                return
                            }
                            val cx = hx.eventType
                            if (cx == c) {
                                handlers = handlers.arrayAdd(handler, i)
                                return
                            }
                            if (cx.canAssignedBy(c)) {
                                handlers = handlers.arrayAdd(handler, i)
                                return
                            }
                            if (c.canAssignedBy(cx)) {
                                i++
                                continue
                            }
                            handlers = handlers.arrayAdd(handler, i)
                            return
                        }
                        handlers = handlers.arrayAdd(handler, i)
                        return
                    }
                    i++
                }
                handlers = handlers.arrayAdd(handler, 0)
            }

            @Synchronized
            override fun unregister(handler: Handler<*>) {
                var i = 0
                while (i < handlers.size) {
                    val hi = handlers[i]
                    if (hi == handler) {
                        handlers = handlers.arrayRemove(i)
                        return
                    }
                    i++
                }
            }

            override fun post(event: Any) {
                val type = event.javaClass
                for (handler in handlers) {
                    val h = handler.asType<Handler<Any>>()
                    val ht = handler.eventType
                    if (ht.canAssignedBy(type)) {
                        executor.execute {
                            h.doEvent(event)
                        }
                    }
                }
            }
        }
    }
}
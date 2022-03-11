package xyz.srclab.common.bus

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.collect.arrayAdd
import xyz.srclab.common.collect.arrayRemove
import xyz.srclab.common.reflect.canAssignedBy
import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.Executor

/**
 * Event bus. Use [register] or [registerAll] to register event handler, [unregister] and [unregisterAll] to unregister.
 *
 * [EventBusHandler] is used to handle event.
 * [EventBus] will post event for each [EventBusHandler] of which [EventBusHandler.eventType] is assignable from event,
 * in sub-to-super order.
 */
interface EventBus {

    fun register(handler: EventBusHandler<*>)

    fun registerAll(handlers: Iterable<EventBusHandler<*>>) {
        for (handler in handlers) {
            register(handler)
        }
    }

    fun unregister(handler: EventBusHandler<*>)

    fun unregisterAll(handlers: Iterable<EventBusHandler<*>>) {
        for (handler in handlers) {
            unregister(handler)
        }
    }

    fun post(event: Any)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun newEventBus(executor: Executor = SyncRunner): EventBus {
            return EventBusImpl(executor)
        }

        private class EventBusImpl(
            private val executor: Executor
        ) : EventBus {

            private var handlers: Array<EventBusHandler<*>> = emptyArray()

            @Synchronized
            override fun register(handler: EventBusHandler<*>) {
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
            override fun unregister(handler: EventBusHandler<*>) {
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
                    val h = handler.asTyped<EventBusHandler<Any>>()
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
package xyz.srclab.common.bus

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.collect.add
import xyz.srclab.common.collect.remove
import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.Executor

/**
 * Event bus. Use [register] or [registerAll] to register event handler, [unregister] and [unregisterAll] to unregister.
 *
 * For event handler, each method annotated by [SubscribeMethod] will be seen as `Subscriber`.
 * `Subscriber` must have only one parameter, and parameter's [Class] is its event type.
 * [EventBus] will post given event for all compatible event type.
 *
 * @see SubscribeMethod
 */
interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun registerAll(eventHandlers: Iterable<EventHandler<*>>) {
        for (eventHandler in eventHandlers) {
            register(eventHandler)
        }
    }

    fun unregister(eventHandler: EventHandler<*>)

    fun unregisterAll(eventHandlers: Iterable<EventHandler<*>>) {
        for (eventHandler in eventHandlers) {
            unregister(eventHandler)
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

            private var handlers: Array<EventHandler<*>> = emptyArray()

            @Synchronized
            override fun register(eventHandler: EventHandler<*>) {
                var i = 0
                while (i < handlers.size) {
                    val hi = handlers[i]
                    if (hi == eventHandler) {
                        handlers[i] = eventHandler
                        return
                    }
                    val ci = hi.eventType
                    val c = eventHandler.eventType
                    if (ci == c) {
                        handlers = handlers.add(eventHandler, i)
                        return
                    }
                    if (ci.isAssignableFrom(c)) {
                        handlers = handlers.add(eventHandler, i)
                        return
                    }
                    if (c.isAssignableFrom(ci)) {
                        i++
                        while (i < handlers.size) {
                            val hx = handlers[i]
                            if (hx == eventHandler) {
                                handlers[i] = eventHandler
                                return
                            }
                            val cx = hx.eventType
                            if (cx == c) {
                                handlers = handlers.add(eventHandler, i)
                                return
                            }
                            if (cx.isAssignableFrom(c)) {
                                handlers = handlers.add(eventHandler, i)
                                return
                            }
                            if (c.isAssignableFrom(cx)) {
                                i++
                                continue
                            }
                            handlers = handlers.add(eventHandler, i)
                            return
                        }
                        handlers = handlers.add(eventHandler, i)
                        return
                    }
                    i++
                }
                handlers = handlers.add(eventHandler, 0)
            }

            @Synchronized
            override fun unregister(eventHandler: EventHandler<*>) {
                var i = 0
                while (i < handlers.size) {
                    val hi = handlers[i]
                    if (hi == eventHandler) {
                        handlers = handlers.remove(i)
                        return
                    }
                    i++
                }
            }

            override fun post(event: Any) {
                val type = event.javaClass
                for (handler in handlers) {
                    val h = handler.asTyped<EventHandler<Any>>()
                    val ht = handler.eventType
                    if (ht.isAssignableFrom(type)) {
                        executor.execute {
                            h.doEvent(event)
                        }
                    }
                }
            }
        }
    }
}
package xyz.srclab.common.bus

import xyz.srclab.common.base.asAny
import xyz.srclab.common.run.Runner
import xyz.srclab.jvm.compile.INAPPLICABLE_JVM_NAME
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun unregister(eventHandler: EventHandler<*>)

    fun <T : Any> emit(event: T) {
        return emit(event.javaClass, event)
    }

    fun <T : Any> emit(eventType: Any, event: T) {
        try {
            return emitOrThrow(eventType, event)
        } catch (e: Exception) {
            //Do nothing
        }
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emitOrThrow(event: T) {
        return emitOrThrow(event.javaClass, event)
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emitOrThrow(eventType: Any, event: T)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun newEventBus(
            handlers: Map<Any, EventHandler<*>>,
            executor: Executor = Runner.SYNC_RUNNER,
            mutable: Boolean = true,
        ): EventBus {
            return if (mutable)
                MutableEventBusImpl(executor, ConcurrentHashMap(handlers))
            else
                EventBusImpl(handlers.toMap(), executor)
        }

        fun Executor.newEventBus(handlers: Map<Any, EventHandler<*>>, mutable: Boolean = true): EventBus {
            return EventBus.newEventBus(handlers, this, mutable)
        }
    }
}

interface EventHandler<T : Any> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val eventType: Any
        @JvmName("eventType") get

    fun handle(event: T)
}

class EventHandlerNotFoundException(eventType: Any) : RuntimeException(eventType.toString())

private abstract class BaseEventBus<M : Map<Any, EventHandler<*>>>(
    handlers: M, private val executor: Executor,
) : EventBus {

    protected val eventHandlerMap: M = handlers

    override fun register(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any> emitOrThrow(eventType: Any, event: T) {
        val eventHandler: EventHandler<T>? = eventHandlerMap[eventType].asAny()
        if (eventHandler === null) {
            throw EventHandlerNotFoundException(eventType)
        }
        try {
            executor.execute { eventHandler.handle(event) }
        } catch (e: Exception) {
            throw e
        }
    }
}

private class EventBusImpl(
    handlers: Map<Any, EventHandler<*>>,
    executor: Executor,
) : BaseEventBus<Map<Any, EventHandler<*>>>(handlers, executor)

private class MutableEventBusImpl(executor: Executor, handlers: MutableMap<Any, EventHandler<*>>) :
    BaseEventBus<MutableMap<Any, EventHandler<*>>>(handlers, executor) {

    override fun register(eventHandler: EventHandler<*>) {
        eventHandlerMap[eventHandler.eventType] = eventHandler
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        eventHandlerMap.remove(eventHandler.eventType)
    }
}
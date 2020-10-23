package xyz.srclab.common.bus

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.asAny
import xyz.srclab.common.run.Runner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun unregister(eventHandler: EventHandler<*>)

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emit(event: T) {
        return emit(event.javaClass, event)
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emit(eventType: Any, event: T)

    companion object {

        @JvmStatic
        fun sync(): EventBus {
            return MutableEventBusImpl(Runner.SYNC_RUNNER)
        }

        @JvmStatic
        fun async(executor: Executor, mutable: Boolean = false): EventBus {
            return MutableEventBusImpl(executor)
        }

        @JvmStatic
        fun fixedSync(handlers: Map<Any, EventHandler<*>>): EventBus {
            return EventBusImpl(Runner.SYNC_RUNNER, handlers.toMap())
        }

        @JvmStatic
        fun fixedSyncAsync(executor: Executor, handlers: Map<Any, EventHandler<*>>): EventBus {
            return EventBusImpl(executor, handlers.toMap())
        }
    }
}

interface EventHandler<T : Any> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val eventType: Any
        @JvmName("eventType") get

    fun handle(event: T);
}

class EventHandlerNotFoundException(eventType: Any) : RuntimeException(eventType.toString())

private abstract class BaseEventBus<M : Map<Any, EventHandler<*>>>(private val executor: Executor) : EventBus {

    protected abstract val eventHandlerMap: M

    override fun register(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any> emit(eventType: Any, event: T) {
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
    executor: Executor,
    override val eventHandlerMap: Map<Any, EventHandler<*>>
) : BaseEventBus<Map<Any, EventHandler<*>>>(executor)

private class MutableEventBusImpl(private val executor: Executor) :
    BaseEventBus<MutableMap<Any, EventHandler<*>>>(executor) {

    override val eventHandlerMap: MutableMap<Any, EventHandler<*>> = ConcurrentHashMap()

    override fun register(eventHandler: EventHandler<*>) {
        eventHandlerMap[eventHandler.eventType] = eventHandler
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        eventHandlerMap.remove(eventHandler.eventType)
    }
}
package xyz.srclab.common.bus

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.asAny
import xyz.srclab.common.run.Runner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

/**
 * Event bus.
 *
 * @see EventHandler
 */
interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun unregister(eventHandler: EventHandler<*>)

    @JvmDefault
    fun <T : Any> emit(event: T) {
        return emit(event.javaClass, event)
    }

    @JvmDefault
    fun <T : Any> emit(eventType: Any, event: T) {
        try {
            return emitOrThrow(eventType, event)
        } catch (e: Exception) {
            //Do nothing
        }
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    @JvmDefault
    fun <T : Any> emitOrThrow(event: T) {
        return emitOrThrow(event.javaClass, event)
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emitOrThrow(eventType: Any, event: T)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun newEventBus(
            handlers: Iterable<EventHandler<*>>,
            executor: Executor = Runner.SYNC_RUNNER,
            mutable: Boolean = true,
        ): EventBus {
            return if (mutable)
                MutableEventBusImpl(handlers, executor)
            else
                EventBusImpl(handlers, executor)
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

abstract class AbstractEventBus(private val executor: Executor) : EventBus {

    protected abstract val handlersMapper: Map<Any, EventHandler<*>>

    override fun register(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any> emitOrThrow(eventType: Any, event: T) {
        val eventHandler: EventHandler<T>? = handlersMapper[eventType].asAny()
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
    handlers: Iterable<EventHandler<*>>,
    executor: Executor,
    override val handlersMapper: Map<Any, EventHandler<*>> = handlers.toMap()
) : AbstractEventBus(executor)

private class MutableEventBusImpl(
    handlers: Iterable<EventHandler<*>>,
    executor: Executor,
    override val handlersMapper: MutableMap<Any, EventHandler<*>> = handlers.toMutableMap()
) : AbstractEventBus(executor) {

    override fun register(eventHandler: EventHandler<*>) {
        handlersMapper[eventHandler.eventType] = eventHandler
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        handlersMapper.remove(eventHandler.eventType)
    }
}

private fun Iterable<EventHandler<*>>.toMap(): Map<Any, EventHandler<*>> {
    val map = HashMap<Any, EventHandler<*>>()
    map.putAllHandlers(this)
    return map
}

private fun Iterable<EventHandler<*>>.toMutableMap(): MutableMap<Any, EventHandler<*>> {
    val map = ConcurrentHashMap<Any, EventHandler<*>>()
    map.putAllHandlers(this)
    return map
}

private fun MutableMap<Any, EventHandler<*>>.putAllHandlers(handlers: Iterable<EventHandler<*>>) = apply {
    for (handler in handlers) {
        this[handler.eventType] = handler
    }
}
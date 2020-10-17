package xyz.srclab.common.bus

import xyz.srclab.common.base.asAny
import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun unregister(eventHandler: EventHandler<*>)

    @Throws(RejectedExecutionException::class)
    fun <T : Any> emit(event: T)

    /**
     * @param onError cause may be [RejectedExecutionException], [EventHandlerNotFoundException]
     */
    fun <T : Any> emit(event: T, onError: (cause: Exception) -> Unit)

    companion object {

        private val syncEventBus: EventBus by lazy { EventBusImpl(SyncRunner) }

        @JvmStatic
        fun sync(): EventBus = syncEventBus

        @JvmStatic
        fun async(executor: Executor): EventBus {
            return EventBusImpl(executor)
        }
    }
}

fun syncEventBus(): EventBus {
    return EventBus.sync()
}

fun asyncEventBus(executor: Executor): EventBus {
    return EventBus.async(executor)
}

private class EventBusImpl(private val executor: Executor) : EventBus {

    private val eventHandlerMap: MutableMap<Class<*>, EventHandler<*>> = ConcurrentHashMap()

    override fun register(eventHandler: EventHandler<*>) {
        eventHandlerMap[eventHandler.eventType] = eventHandler
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        eventHandlerMap.remove(eventHandler.eventType)
    }

    override fun <T : Any> emit(event: T) {
        val eventHandler: EventHandler<T>? = eventHandlerMap[event::class.java].asAny()
        if (eventHandler === null) {
            return
        }
        executor.execute { eventHandler.handle(event) }
    }

    override fun <T : Any> emit(event: T, onError: (cause: Exception) -> Unit) {
        val eventType = event::class.java
        val eventHandler: EventHandler<T>? = eventHandlerMap[eventType].asAny()
        if (eventHandler === null) {
            val eventHandlerNotFoundException = EventHandlerNotFoundException(eventType)
            onError(eventHandlerNotFoundException)
            return
        }
        try {
            executor.execute { eventHandler.handle(event) }
        } catch (e: Exception) {
            onError(e)
            return
        }
    }
}

interface EventHandler<T : Any> {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val eventType: Class<*>
        @JvmName("eventType") get

    fun handle(event: T);
}

class EventHandlerNotFoundException(eventType: Class<*>) : RuntimeException(eventType.toString())
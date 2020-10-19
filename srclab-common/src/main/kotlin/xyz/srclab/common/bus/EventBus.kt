package xyz.srclab.common.bus

import xyz.srclab.common.base.asAny
import xyz.srclab.common.run.SyncRunner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

interface EventBus {

    fun register(eventHandler: EventHandler<*>)

    fun unregister(eventHandler: EventHandler<*>)

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emit(event: T) {
        return emit(event::class.java, event)
    }

    @Throws(EventHandlerNotFoundException::class, RejectedExecutionException::class)
    fun <T : Any> emit(eventType: Class<*>, event: T)

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

private class EventBusImpl(private val executor: Executor) : EventBus {

    private val eventHandlerMap: MutableMap<Class<*>, EventHandler<*>> = ConcurrentHashMap()

    override fun register(eventHandler: EventHandler<*>) {
        eventHandlerMap[eventHandler.eventType] = eventHandler
    }

    override fun unregister(eventHandler: EventHandler<*>) {
        eventHandlerMap.remove(eventHandler.eventType)
    }

    override fun <T : Any> emit(eventType: Class<*>, event: T) {
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

interface EventHandler<T : Any> {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val eventType: Class<*>
        @JvmName("eventType") get

    fun handle(event: T);
}

class EventHandlerNotFoundException(eventType: Class<*>) : RuntimeException(eventType.toString())
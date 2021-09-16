package xyz.srclab.common.bus

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.base.INHERITANCE_COMPARATOR
import xyz.srclab.common.run.Runner
import java.util.*
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

    fun register(eventHandler: Any)

    fun registerAll(eventHandlers: Iterable<Any>)

    fun unregister(eventHandler: Any)

    fun unregisterAll(eventHandlers: Iterable<Any>)

    fun post(event: Any)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun newEventBus(
            executor: Executor = Runner.SYNC_RUNNER,
        ): EventBus {
            return EventBusImpl(executor)
        }

        private class EventBusImpl(
            private val executor: Executor
        ) : EventBus {

            private var subscribeMap: TreeMap<Class<*>, MutableCollection<Action>> = TreeMap(COMPARATOR)

            override fun register(eventHandler: Any) {
                val newActions = resolveHandler(eventHandler)
                if (newActions.isEmpty()) {
                    return
                }
                addActions(newActions)
            }

            override fun registerAll(eventHandlers: Iterable<Any>) {
                val totalActions: MutableMap<Class<*>, MutableList<Action>> = HashMap()
                for (eventHandler in eventHandlers) {
                    val newActions = resolveHandler(eventHandler)
                    for (newAction in newActions) {
                        val actions = totalActions.getOrPut(newAction.key) { LinkedList() }
                        actions.addAll(newAction.value)
                    }
                }
                addActions(totalActions)
            }

            private fun addActions(newActions: Map<Class<*>, List<Action>>) {
                synchronized(this) {
                    val newMap = copySubscribers()
                    for (newAction in newActions) {
                        val eventType = newAction.key
                        val actions = newAction.value
                        val oldActions = newMap.getOrPut(eventType) { ArrayList() }
                        oldActions.addAll(actions)
                    }
                    subscribeMap = newMap
                }
            }

            override fun unregister(eventHandler: Any) {
                unregisterAll(listOf(eventHandler))
            }

            override fun unregisterAll(eventHandlers: Iterable<Any>) {
                val handlerSet = eventHandlers.toSet()
                synchronized(this) {
                    val newMap = copySubscribers()
                    for (entry in newMap) {
                        val actions = entry.value
                        val it = actions.iterator()
                        while (it.hasNext()) {
                            val action = it.next()
                            if (handlerSet.contains(action.handler)) {
                                it.remove()
                            }
                        }
                    }
                    subscribeMap = newMap
                }
            }

            private fun copySubscribers(): TreeMap<Class<*>, MutableCollection<Action>> {
                val newMap = TreeMap<Class<*>, MutableCollection<Action>>(subscribeMap.comparator())
                for (mutableEntry in subscribeMap) {
                    val newList = LinkedList<Action>()
                    newList.addAll(mutableEntry.value)
                    newMap[mutableEntry.key] = newList
                }
                return newMap
            }

            private fun resolveHandler(eventHandler: Any): Map<Class<*>, List<Action>> {
                val result: MutableMap<Class<*>, MutableList<Action>> = HashMap()
                for (method in eventHandler.javaClass.methods) {
                    val subscribe = method.getAnnotation(SubscribeMethod::class.java)
                    if (subscribe === null || method.isBridge) {
                        continue
                    }
                    if (method.parameterCount != 1) {
                        throw IllegalArgumentException("Subscribe method must have only one parameter.")
                    }
                    val eventType = method.parameterTypes[0]
                    val action = Action(eventHandler, subscribe, Invoker.forMethod(method))
                    val actions = result.getOrPut(eventType) { LinkedList() }
                    actions.add(action)
                }
                return result
            }

            override fun post(event: Any) {
                executor.execute { post0(event) }
            }

            private fun post0(event: Any) {
                val subscribers = subscribeMap
                    .subMap(Any::class.java, true, event.javaClass, true)
                    .descendingMap()
                for (subscriberEntry in subscribers) {
                    if (!subscriberEntry.key.isAssignableFrom(event.javaClass)) {
                        continue
                    }
                    val actions = subscriberEntry.value
                    for (action in actions) {
                        executor.execute {
                            action.invoker.invoke<Any?>(action.handler, event)
                        }
                    }
                }
            }

            private data class Action(
                val handler: Any,
                val subscribeMethod: SubscribeMethod,
                val invoker: Invoker,
            )

            companion object {
                private val COMPARATOR: Comparator<Class<*>> = Comparator { c1, c2 ->
                    val r0 = -INHERITANCE_COMPARATOR.compare(c1, c2)
                    if (r0 == 0) {
                        if (c1 == c2) {
                            return@Comparator r0
                        }
                        return@Comparator 1
                    }
                    r0
                }
            }
        }
    }
}
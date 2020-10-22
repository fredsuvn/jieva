package xyz.srclab.common.base

/**
 * @author sunqian
 */
interface Current {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val thread: Thread
        @JvmName("thread") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val classLoader: ClassLoader
        @JvmName("classLoader") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val context: MutableMap<Any, Any?>
        @JvmName("context") get

    fun get(key: Any): Any {
        return context[key]!!
    }

    fun getOrNull(key: Any): Any? {
        return context[key]
    }

    fun <T : Any> getAs(key: Any): T {
        return get(key).asAny()
    }

    fun <T : Any> getAsOrNull(key: Any): T? {
        return getOrNull(key).asAny()
    }

    fun set(key: Any, value: Any?) {
        context[key] = value
    }

    fun clearContext() {
        context.clear()
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun current(thread: Thread = currentThread()): Current {
            return CurrentImpl(thread)
        }

        @JvmStatic
        fun currentThread(): Thread {
            return Thread.currentThread()
        }

        @JvmStatic
        fun currentClassLoader(): ClassLoader {
            return Thread.currentThread().contextClassLoader
        }

        @JvmStatic
        fun currentContext(): MutableMap<Any, Any?> {
            return localContext.get()
        }
    }
}

fun current(thread: Thread = currentThread()): Current {
    return Current.current(thread)
}

fun currentThread(): Thread {
    return Current.currentThread()
}

fun currentClassLoader(): ClassLoader {
    return Current.currentClassLoader()
}

fun currentContext(): MutableMap<Any, Any?> {
    return Current.currentContext()
}

private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
    ThreadLocal.withInitial { mutableMapOf() }
}

private class CurrentImpl(override val thread: Thread) : Current {

    override val classLoader: ClassLoader = thread.contextClassLoader

    override val context: MutableMap<Any, Any?>
        get() = localContext.get()
}
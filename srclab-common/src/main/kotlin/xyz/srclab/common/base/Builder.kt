package xyz.srclab.common.base

import java.util.*

abstract class BaseCachingProductBuilder<T : Any> {

    private var cache: T? = null
    private var state = 0
    private var buildState = 0

    protected fun buildCaching(): T {
        if (cache === null || state != buildState) {
            cache = buildNew()
            updateChange()
        }
        return cache.asNotNull()
    }

    /**
     * Called after any change which leads to refresh cache.
     */
    protected fun commitChange() {
        state++
        if (state == buildState) {
            state++
        }
    }

    private fun updateChange() {
        buildState = state
    }

    protected abstract fun buildNew(): T
}

abstract class CachingProductBuilder<T : Any> : BaseCachingProductBuilder<T>() {

    fun build(): T {
        return buildCaching()
    }
}

abstract class HandlersCachingProductBuilder<T : Any, H, B : HandlersCachingProductBuilder<T, H, B>> :
    BaseCachingProductBuilder<T>() {

    private var handlers: MutableList<H>? = null

    fun addHandler(handler: H): B {
        notNullHandlers().add(handler)
        commitChange()
        return this.asAny()
    }

    fun addHandlers(vararg handlers: H): B {
        notNullHandlers().addAll(handlers)
        commitChange()
        return this.asAny()
    }

    fun addHandlers(handlers: Iterable<H>): B {
        notNullHandlers().addAll(handlers)
        commitChange()
        return this.asAny()
    }

    fun handlers(): List<H> {
        if (handlers === null) {
            return emptyList()
        }
        return handlers!!.toList()
    }

    fun build(): T {
        return buildCaching()
    }

    private fun notNullHandlers(): MutableList<H> {
        if (handlers === null) {
            handlers = LinkedList()
        }
        return handlers.asNotNull()
    }
}
package xyz.srclab.common.test

import xyz.srclab.common.base.Current.callerFrame
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

interface TestMarker {

    @JvmDefault
    fun mark(value: Any) {
        val key = this.javaClass.callerFrame() ?: ("AutoMarkKey-${markKeyCounter.getAndIncrement()}")
        mark(key, value)
    }

    @JvmDefault
    fun mark(key: Any, value: Any): Any? {
        return getMarkMap(this).put(key, value)
    }

    @JvmDefault
    fun unmark(key: Any): Any? {
        return getMarkMap(this).remove(key)
    }

    @JvmDefault
    fun getMark(key: Any): Any? {
        return getMarkMap(this)
    }

    @JvmDefault
    fun clearMarks() {
        getMarkMap(this).clear()
    }

    @JvmDefault
    fun asMap(): MutableMap<Any, Any> {
        return getMarkMap(this)
    }

    companion object {

        private val markPool = ConcurrentHashMap<TestMarker, ConcurrentHashMap<Any, Any>>()

        private val markKeyCounter = AtomicLong(0)

        @JvmStatic
        fun newTestMarker(): TestMarker {
            return object : TestMarker {
                override fun toString(): String {
                    return asMap().toString()
                }
            }
        }

        private fun getMarkMap(testMarker: TestMarker): ConcurrentHashMap<Any, Any> {
            return markPool.computeIfAbsent(testMarker) { ConcurrentHashMap() }
        }
    }
}
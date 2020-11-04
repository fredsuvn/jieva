package xyz.srclab.test.mark

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

interface TestMarker {

    fun mark(value: Any) {
        val key = createCutPoint()
        mark(key, value)
    }

    fun mark(key: Any, value: Any): Any? {
        return getMarkMap(this).put(key, value)
    }

    fun unmark(key: Any): Any? {
        return getMarkMap(this).remove(key)
    }

    fun getMark(key: Any): Any? {
        return getMarkMap(this)
    }

    fun clearMarks() {
        getMarkMap(this).clear()
    }

    fun asMap(): MutableMap<Any, Any> {
        return getMarkMap(this)
    }

    companion object {

        @JvmStatic
        fun newTestMarker(): TestMarker {
            return object : TestMarker {
                override fun toString(): String {
                    return asMap().toString()
                }
            }
        }

        private val markPool = ConcurrentHashMap<TestMarker, ConcurrentHashMap<Any, Any>>()

        private val counter = AtomicLong(0)

        private fun getMarkMap(testMarker: TestMarker): ConcurrentHashMap<Any, Any> {
            return markPool.computeIfAbsent(testMarker) { ConcurrentHashMap() }
        }

        private fun createCutPoint(): Any {
            val throwable = Throwable()
            val stackTrace = throwable.stackTrace
            if (stackTrace.isNullOrEmpty()) {
                return "Failed to get stack trace (${counter.getAndIncrement()})."
            }
            return if (stackTrace.size >= 4)
                stackTrace[4]
            else
                "Stack trace is too short (${counter.getAndIncrement()})."
        }
    }
}
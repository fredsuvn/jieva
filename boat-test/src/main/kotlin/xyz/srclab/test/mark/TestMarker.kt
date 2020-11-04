package xyz.srclab.test.mark

import xyz.srclab.jvm.reflect.findCallerFrame
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

interface TestMarker {

    fun mark(value: Any) {
        val key = this.javaClass.findCallerFrame() ?: ("AutoMarkKey-${markKeyCounter.getAndIncrement()}")
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
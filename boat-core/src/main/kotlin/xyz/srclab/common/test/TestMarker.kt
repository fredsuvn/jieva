package xyz.srclab.common.test

import xyz.srclab.common.lang.asAny
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * To mark values into third party.
 */
interface TestMarker {

    fun <T> mark(key: Any, value: Any): T {
        return getMarkMap(this).put(key, value).asAny()
    }

    fun <T> unmark(key: Any): T {
        return getMarkMap(this).remove(key).asAny()
    }

    fun <T> getMark(key: Any): T {
        return getMarkMap(this)[key].asAny()
    }

    fun clearMarks() {
        getMarkMap(this).clear()
    }

    fun asMap(): MutableMap<Any, Any?> {
        return getMarkMap(this).asAny()
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
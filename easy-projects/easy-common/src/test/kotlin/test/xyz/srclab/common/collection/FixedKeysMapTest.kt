package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import test.xyz.srclab.common.Config
import xyz.srclab.common.collection.map.FixedKeysMap
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable
import kotlin.random.Random

/**
 * @author sunqian
 */
object FixedKeysMapTest {

    private val fixedKeysMap = FixedKeysMap(mapDataProvider())

    private val hashMap = HashMap<String, String>(mapDataProvider())

    @Test
    fun testMap() {
        val hashMap = mapOf(1 to 1, 2 to 2)
        val fixed: MutableMap<Int, Int?> = FixedKeysMap(mapOf(1 to 1, 2 to 2))

        doAssertEquals(fixed, hashMap)
        doAssertEquals(fixed == mapOf(1 to 1, 2 to 2), true)
        doAssertEquals(fixed == mapOf(1 to 1), false)
        doAssertEquals(fixed == fixed, true)
        doAssertEquals(fixed.entries, hashMap.entries)
        doAssertEquals(fixed.keys, hashMap.keys)
        doAssertEquals(fixed.values, hashMap.values)
        doAssertEquals(fixed.isEmpty(), false)
        doAssertEquals(fixed.containsKey(1), true)
        doAssertEquals(fixed.containsKey(3), false)
        doAssertEquals(fixed.containsValue(8), false)
        doAssertEquals(fixed.hashCode(), hashMap.hashCode())

        fixed[1] = 8
        doAssertEquals(fixed[1], 8)
        doAssertEquals(fixed.containsValue(8), true)
        fixed.putAll(mapOf(1 to 2, 2 to 3))
        doAssertEquals(fixed, mapOf(1 to 2, 2 to 3))

        doExpectThrowable(UnsupportedOperationException::class.java) {
            fixed[3] = 3
        }
        doExpectThrowable(UnsupportedOperationException::class.java) {
            fixed.putAll(mapOf(1 to 2, 2 to 3, 3 to 4))
        }
        doExpectThrowable(UnsupportedOperationException::class.java) {
            fixed.remove(1)
        }
        doExpectThrowable(UnsupportedOperationException::class.java) {
            fixed[1] = null
        }
        doExpectThrowable(UnsupportedOperationException::class.java) {
            fixed.clear()
        }
    }


    @Test(
        enabled = Config.enableConcurrent,
        invocationCount = 100000,
        threadPoolSize = 500
    )
    fun testMapConcurrent() {
        runTest(fixedKeysMap)
    }

    private fun runTest(map: MutableMap<String, String>) {
        val key = Random.nextLong(1, 2).toString()
        val value = Random.nextLong(1, 100000).toString()
        map[key] = value
        Thread.sleep(1)
        val expectedValue = value
        val actualValue = map[key]
        if (expectedValue != actualValue) {
            print("${Thread.currentThread()}: ")
            println("key: ${key}, expectedValue: ${expectedValue}, actualValue: ${actualValue}")
        }
    }

    private fun mapDataProvider(): Map<String, String> {
        val mutableMap = HashMap<String, String>()
        for (i in 1..1000) {
            mutableMap[i.toString()] = i.toString()
        }
        return mutableMap
    }
}
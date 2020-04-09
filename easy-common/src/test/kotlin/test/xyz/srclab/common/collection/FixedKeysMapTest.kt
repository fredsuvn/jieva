package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.common.collection.map.FixedKeysMap
import kotlin.random.Random

/**
 * @author sunqian
 */
object FixedKeysMapTest {

    private val fixedKeysMap = FixedKeysMap<String, String>(mapDataProvider())

    private val hashMap = HashMap<String, String>(mapDataProvider())


    @Test(
//        enabled = false,
        invocationCount = 100000,
        threadPoolSize = 500
    )
    fun testMap() {
        runTest(hashMap)
    }

    private fun runTest(map: MutableMap<String, String>) {
        val key = Random.nextLong(1, 2).toString()
        val value = Random.nextLong(1, 100000).toString()
        map[key] = value
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
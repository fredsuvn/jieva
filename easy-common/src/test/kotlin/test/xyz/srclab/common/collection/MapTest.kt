package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * @author sunqian
 */
object MapTest {

    private val concurrentHashMap = ConcurrentHashMap<String, String>()

    private val hashMap = HashMap<String, String>()


    @Test(
        enabled = false,
        invocationCount = 10000,
        threadPoolSize = 500
    )
    fun testMap() {
        runTest(concurrentHashMap)
    }

    private fun runTest(map: MutableMap<String, String>) {
        val value = Random.nextLong(1000).toString()
        map[value] = value
        doAssertEquals(map[value], value)
    }
}
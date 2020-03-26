package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssert
import xyz.srclab.common.lang.Computed
import java.util.concurrent.TimeUnit

object ComputedTest {

    @Test
    fun testNoTimeout() {
        val count = arrayOf(0)
        val computed = Computed.with {
            count[0]++
            count[0]
        }
        println(computed.get())
        println(computed.get())
        println(computed.get())
        doAssert(computed.get(), 1)
        doAssert(computed.refreshAndGet(), 2)
    }

    @Test
    fun testTimeout() {
        val count = arrayOf(0)
        val computed = Computed.with(1, TimeUnit.MILLISECONDS) {
            count[0]++
            count[0]
        }
        println(computed.get())
        Thread.sleep(1000)
        println(computed.get())
        Thread.sleep(1000)
        println(computed.get())
        println(computed.get())
        println(computed.get())
        Thread.sleep(1000)
        doAssert(computed.get(), 4)
        doAssert(computed.refreshAndGet(), 5)
    }
}
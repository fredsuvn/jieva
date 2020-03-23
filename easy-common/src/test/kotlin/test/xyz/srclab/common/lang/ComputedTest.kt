package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.doTest
import xyz.srclab.common.lang.Computed

object ComputedTest {

    @Test
    fun testNoTimeout() {
        val count = arrayOf(0)
        val computed = Computed.of {
            count[0]++
            count[0]
        }
        println(computed.get())
        println(computed.get())
        println(computed.get())
        doTest(computed.get(), 1)
        doTest(computed.refreshAndGet(), 2)
    }

    @Test
    fun testTimeout() {
        val count = arrayOf(0)
        val computed = Computed.of(1000) {
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
        doTest(computed.get(), 4)
        doTest(computed.refreshAndGet(), 5)
    }
}
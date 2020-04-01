package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.lang.Computed
import java.time.Duration

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
        doAssertEquals(computed.get(), 1)
        doAssertEquals(computed.refreshAndGet(), 2)
    }

    @Test
    fun testTimeout() {
        val count = arrayOf(0)
        val computed = Computed.with(Duration.ofMillis(1)) {
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
        doAssertEquals(computed.get(), 4)
        doAssertEquals(computed.refreshAndGet(), 5)
    }
}
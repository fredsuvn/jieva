package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.Config
import xyz.srclab.test.doAssertEquals
import java.time.Duration

object ComputedTest {

    @Test
    fun testNoTimeout() {
        val count = arrayOf(0)
        val computed = Computed.with {
            count[0]++
            count[0]
        }
        doAssertEquals(computed.get(), 1)
        doAssertEquals(computed.get(), 1)
        doAssertEquals(computed.refreshGet(), 2)
    }

    @Test(
        enabled = Config.enableBlocked
    )
    fun testTimeout() {
        val count = arrayOf(0)
        val count2 = arrayOf(0)
        val computed = Computed.with(1) {
            count[0]++
            count[0]
        }
        val computed2 = Computed.with(Duration.ofMillis(1)) {
            count2[0]++
            count2[0]
        }
        doAssertEquals(computed.get(), 1)
        doAssertEquals(computed2.get(), 1)
        Thread.sleep(1100)
        doAssertEquals(computed.get(), 2)
        doAssertEquals(computed2.get(), 2)
        Thread.sleep(1100)
        doAssertEquals(computed.get(), 3)
        doAssertEquals(computed2.get(), 3)
        Thread.sleep(1100)
        doAssertEquals(computed.get(), 4)
        doAssertEquals(computed.get(), 4)
        doAssertEquals(computed2.get(), 4)
        doAssertEquals(computed.refreshGet(), 5)
        doAssertEquals(computed2.refreshGet(), 5)
    }
}
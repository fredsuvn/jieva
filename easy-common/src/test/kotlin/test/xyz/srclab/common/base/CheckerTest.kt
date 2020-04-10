package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.test.doExpectThrowable
import xyz.srclab.common.base.Checker

/**
 * @author sunqian
 */
object CheckerTest {

    @Test
    fun testCheckIndexFromTo() {
        doExpectThrowable(IndexOutOfBoundsException::class.java) {
            Checker.checkBoundsFromTo(0, 1, 2)
        }
    }
}
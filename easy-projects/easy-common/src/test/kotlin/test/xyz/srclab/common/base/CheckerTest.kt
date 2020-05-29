package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.Checker
import xyz.srclab.test.doExpectThrowable

/**
 * @author sunqian
 */
object CheckerTest {

    @Test
    fun testCheckIndexFromTo() {
        doExpectThrowable(IndexOutOfBoundsException::class.java) {
            Checker.checkBounds(0, 1, 2)
        }
    }
}
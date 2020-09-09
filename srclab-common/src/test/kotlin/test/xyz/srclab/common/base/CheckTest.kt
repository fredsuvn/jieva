package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.Check
import xyz.srclab.test.doExpectThrowable

/**
 * @author sunqian
 */
object CheckTest {

    @Test
    fun testCheckIndexFromTo() {
        doExpectThrowable(IndexOutOfBoundsException::class.java) {
            Check.checkRangeInBounds(1, 2, 0)
        }
    }
}
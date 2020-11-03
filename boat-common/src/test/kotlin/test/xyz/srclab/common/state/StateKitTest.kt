package test.xyz.srclab.common.state

import org.testng.annotations.Test
import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.state.StateKit
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object StateKitTest {

    @Test
    fun testEquals() {
        val state = ExceptionStatus.newExceptionStatus("")
        doAssertEquals(StateKit.equals(state, state), true)
        doAssertEquals(StateKit.equals(state, ""), false)
    }
}
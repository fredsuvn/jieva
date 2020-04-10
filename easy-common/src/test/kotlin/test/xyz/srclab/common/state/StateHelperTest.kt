package test.xyz.srclab.common.state

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.state.StateHelper

/**
 * @author sunqian
 */
object StateHelperTest {

    @Test
    fun testEquals() {
        val state = ExceptionStatus.newExceptionStatus("")
        doAssertEquals(StateHelper.equals(state, state), true)
        doAssertEquals(StateHelper.equals(state, ""), false)
    }
}
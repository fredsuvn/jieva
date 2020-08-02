package test.xyz.srclab.common.state

import org.testng.annotations.Test
import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.state.StateHelper
import xyz.srclab.test.doAssertEquals

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
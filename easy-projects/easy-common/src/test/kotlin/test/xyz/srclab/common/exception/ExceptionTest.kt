package test.xyz.srclab.common.exception

import org.testng.annotations.Test
import xyz.srclab.common.exception.BusinessException
import xyz.srclab.common.exception.DefaultExceptionStatus
import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.exception.ExceptionWrapper
import xyz.srclab.common.state.StateHelper
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object ExceptionTest {

    @Test
    fun testBusinessException() {
        val cause = IllegalStateException()
        val b1 = BusinessException(DefaultExceptionStatus.INTERNAL)
        doAssertEquals(b1.code, DefaultExceptionStatus.INTERNAL.code)
        doAssertEquals(b1.description, DefaultExceptionStatus.INTERNAL.description)
        doAssertEquals(StateHelper.equals(b1, DefaultExceptionStatus.INTERNAL), true)
        doAssertEquals(b1 == b1, true)
        doAssertEquals(
            b1.withMoreDescription("sss"),
            DefaultExceptionStatus.INTERNAL.withMoreDescription("sss")
        )
        doAssertEquals(b1 == cause, false)

        val status = ExceptionStatus.from(DefaultExceptionStatus.INTERNAL)
        doAssertEquals(status, b1)
        doAssertEquals(status.hashCode(), b1.hashCode())
        doAssertEquals(
            status.withMoreDescription("sss"),
            b1.withMoreDescription("sss")
        )

        doAssertEquals(ExceptionStatus.newExceptionStatus("666").code, "666")
        doAssertEquals(ExceptionStatus.newExceptionStatus("666").description, null)
    }

    @Test
    fun testExceptionWrapper() {
        val cause = IllegalStateException()
        val wrapper = ExceptionWrapper(cause)
        doAssertEquals(wrapper.wrapped, cause)
    }
}
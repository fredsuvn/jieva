package test.xyz.srclab.test.asserts

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable

/**
 * @author sunqian
 */
object TesterTest {

    @Test
    fun testAssert() {
        doAssertEquals("1", "1")
        doExpectThrowable(NullPointerException::class.java) {
            throw NullPointerException("123")
        }.catch {
            doAssertEquals(it.message, "123")
        }
    }
}
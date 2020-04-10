package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.lang.Ref

/**
 * @author sunqian
 */
object RefTest {

    @Test
    fun testRef() {
        val ref = Ref.withEmpty<Int>()
        doAssertEquals(ref.get(), null)
        ref.set(1)
        doAssertEquals(ref.get(), 1)

        val ref2 = Ref.with("1")
        doAssertEquals(ref.get(), 1)
    }
}
package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.collection.list.ListHelper

/**
 * @author sunqian
 */
object ListHelperTest {

    @Test
    fun testConcat() {
        val l1 = listOf("1")
        val l2 = setOf("2")
        val result = ListHelper.concat(l1, l2)
        doAssertEquals(result, listOf("1", "2"))
    }
}
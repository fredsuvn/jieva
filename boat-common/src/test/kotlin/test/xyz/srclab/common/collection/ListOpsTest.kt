package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.common.collection.ListOps
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object ListOpsTest {

    @Test
    fun testConcat() {
        val l1 = listOf("1")
        val l2 = setOf("2")
        val result = ListOps.concat(l1, l2)
        doAssertEquals(result, listOf("1", "2"))
    }
}
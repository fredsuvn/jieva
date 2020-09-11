package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.common.collection.IterableOps
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object IterableOpsTest {

    @Test
    fun testAs() {
        val it = listOf(1, 2, 3)
        val list = IterableOps.asList(it)
        val collection = IterableOps.asCollection(it)
        val set = IterableOps.asSet(it)
        doAssertEquals(list, listOf(1, 2, 3))
        doAssertEquals(collection, listOf(1, 2, 3))
        doAssertEquals(set, setOf(1, 2, 3))
    }
}
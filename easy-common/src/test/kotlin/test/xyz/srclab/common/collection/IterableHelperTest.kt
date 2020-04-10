package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.collection.iterable.IterableHelper

/**
 * @author sunqian
 */
object IterableHelperTest {

    @Test
    fun testAs() {
        val it = listOf(1, 2, 3)
        val list = IterableHelper.asList(it)
        val collection = IterableHelper.asCollection(it)
        val set = IterableHelper.asSet(it)
        doAssertEquals(list, listOf(1, 2, 3))
        doAssertEquals(collection, listOf(1, 2, 3))
        doAssertEquals(set, setOf(1, 2, 3))
    }
}
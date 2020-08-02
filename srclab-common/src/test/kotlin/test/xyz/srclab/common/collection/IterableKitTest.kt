package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.common.collection.IterableKit
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object IterableKitTest {

    @Test
    fun testAs() {
        val it = listOf(1, 2, 3)
        val list = IterableKit.asList(it)
        val collection = IterableKit.asCollection(it)
        val set = IterableKit.asSet(it)
        doAssertEquals(list, listOf(1, 2, 3))
        doAssertEquals(collection, listOf(1, 2, 3))
        doAssertEquals(set, setOf(1, 2, 3))
    }
}
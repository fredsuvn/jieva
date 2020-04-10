package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.collection.map.MapHelper

object MapHelperTest {

    @Test
    fun testMap() {
        val source = mapOf(1 to 1, 2 to 2)
        val newMap = MapHelper.map(source, { k -> k.toString() }, { v -> v.toString() })
        doAssertEquals(newMap, mapOf("1" to "1", "2" to "2"))
    }

    @Test
    fun testRemoveAll() {
        val map = mutableMapOf(1 to 1, 2 to 2, 3 to 3)
        MapHelper.removeAll(map, 1, 2)
        doAssertEquals(map, mapOf(3 to 3))
    }
}
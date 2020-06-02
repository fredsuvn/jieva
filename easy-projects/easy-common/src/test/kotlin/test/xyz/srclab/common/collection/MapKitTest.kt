package test.xyz.srclab.common.collection

import org.testng.annotations.Test
import xyz.srclab.common.collection.MapKit
import xyz.srclab.test.doAssertEquals

object MapKitTest {

    @Test
    fun testMap() {
        val source = mapOf(1 to 1, 2 to 2)
        val newMap = MapKit.map(source, { k -> k.toString() }, { v -> v.toString() })
        doAssertEquals(newMap, mapOf("1" to "1", "2" to "2"))
    }

    @Test
    fun testRemoveAll() {
        val map = mutableMapOf(1 to 1, 2 to 2, 3 to 3)
        MapKit.removeAll(map, 1, 2)
        doAssertEquals(map, mapOf(3 to 3))
    }
}
package sample.kotlin.xyz.srclab.common.collect

import org.testng.annotations.Test
import xyz.srclab.common.collect.ListMap.Companion.toListMap
import xyz.srclab.common.collect.MutableListMap.Companion.toMutableListMap
import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
import xyz.srclab.common.collect.SetMap.Companion.toSetMap
import xyz.srclab.common.collect.addElements
import xyz.srclab.common.test.TestLogger
import java.util.*

class CollectSample {

    @Test
    fun testMultiMap() {
        val setMap = mapOf("s" to setOf("1", "2", "3")).toSetMap()
        //setMap: {s=[1, 2, 3]}
        logger.log("setMap: {}", setMap)

        val mutableSetMap = mutableMapOf("s" to mutableSetOf("1", "2", "3")).toMutableSetMap()
        mutableSetMap.add("s", "9")
        mutableSetMap.addAll("s", LinkedHashSet<String>().addElements("11", "12", "13"))
        //mutableSetMap: {s=[1, 2, 3, 9, 11, 12, 13]}
        logger.log("mutableSetMap: {}", mutableSetMap)

        val listMap = mapOf("s" to listOf("1", "2", "3")).toListMap()
        //listMap: {s=[1, 2, 3]}
        logger.log("listMap: {}", listMap)

        val mutableListMap = mutableMapOf("s" to mutableListOf("1", "2", "3")).toMutableListMap()
        mutableListMap.add("s", "9")
        mutableListMap.addAll("s", LinkedList<String>().addElements("11", "12", "13"))
        //mutableListMap: {s=[1, 2, 3, 9, 11, 12, 13]}
        logger.log("mutableListMap: {}", mutableListMap)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
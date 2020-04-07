package test.xyz.srclab.common.bean

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.doExpectThrowable
import xyz.srclab.common.bean.*

object BeanOperatorTest {

    @Test
    fun testCopyProperties() {
        // bean to bean
        val a = A()
        val b = B()
        b.clear()
        BeanHelper.copyProperties(a, b)
        doAssertEquals(b.stringProperty, a.stringProperty)
        doAssertEquals(b.intProperty, a.intProperty)
        doAssertEquals(b.intArray, arrayOf("1", "2", "3"))
        doAssertEquals(b.stringArray, arrayOf(1, 2, 3))
        doAssertEquals(b.stringList, listOf(1, 2, 3))
        doAssertEquals(b.stringSet, setOf(1, 2, 3))
        doAssertEquals(b.stringMap, mapOf(1 to 1, 2 to 2, 3 to 3))
        doAssertEquals(b.listMap, mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5)))
        doAssertEquals(b.listMap2, mapOf(1 to listOf(10, 20, 30), 2 to listOf(20, 30, 40), 3 to listOf(30, 40, 50)))
        doAssertEquals(b.aa?.intString, 110)

        //bean to map
        val map = mutableMapOf<String, Object>()
        BeanHelper.copyProperties(a, map)
        doAssertEquals(map["stringProperty"], a.stringProperty)
        doAssertEquals(map["intProperty"], a.intProperty)
        doAssertEquals(map["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map["aa"] as Aa).intString, "110")

        //map to map
        val map2 = mutableMapOf<String, Object>()
        BeanHelper.copyProperties(map, map2)
        doAssertEquals(map2["stringProperty"], a.stringProperty)
        doAssertEquals(map2["intProperty"], a.intProperty)
        doAssertEquals(map2["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map2["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map2["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map2["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map2["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map2["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map2["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map2["aa"] as Aa).intString, "110")

        //map to bean
        b.clear()
        BeanHelper.copyProperties(map, b)
        doAssertEquals(b.stringProperty, a.stringProperty)
        doAssertEquals(b.intProperty, a.intProperty)
        doAssertEquals(b.intArray, arrayOf("1", "2", "3"))
        doAssertEquals(b.stringArray, arrayOf(1, 2, 3))
        doAssertEquals(b.stringList, listOf(1, 2, 3))
        doAssertEquals(b.stringSet, setOf(1, 2, 3))
        doAssertEquals(b.stringMap, mapOf(1 to 1, 2 to 2, 3 to 3))
        doAssertEquals(b.listMap, mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5)))
        doAssertEquals(b.listMap2, mapOf(1 to listOf(10, 20, 30), 2 to listOf(20, 30, 40), 3 to listOf(30, 40, 50)))
        doAssertEquals(b.aa?.intString, 110)
    }

    @Test
    fun testCopyPropertiesIgnoreNull() {
        // bean to bean
        val a = A()
        val b = B()
        a.stringProperty = null
        BeanHelper.copyPropertiesIgnoreNull(a, b)
        doAssertEquals(b.stringProperty, "B.stringProperty")
        doAssertEquals(b.intProperty, a.intProperty)
        doAssertEquals(b.intArray, arrayOf("1", "2", "3"))
        doAssertEquals(b.stringArray, arrayOf(1, 2, 3))
        doAssertEquals(b.stringList, listOf(1, 2, 3))
        doAssertEquals(b.stringSet, setOf(1, 2, 3))
        doAssertEquals(b.stringMap, mapOf(1 to 1, 2 to 2, 3 to 3))
        doAssertEquals(b.listMap, mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5)))
        doAssertEquals(b.listMap2, mapOf(1 to listOf(10, 20, 30), 2 to listOf(20, 30, 40), 3 to listOf(30, 40, 50)))
        doAssertEquals(b.aa?.intString, 110)

        //bean to map
        val map = mutableMapOf<String, Object>()
        BeanHelper.copyPropertiesIgnoreNull(a, map)
        doAssertEquals(map["stringProperty"], null)
        doAssertEquals(map["intProperty"], a.intProperty)
        doAssertEquals(map["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map["aa"] as Aa).intString, "110")

        //map to map
        val map2 = mutableMapOf<String, Object>()
        BeanHelper.copyPropertiesIgnoreNull(map, map2)
        doAssertEquals(map2["stringProperty"], null)
        doAssertEquals(map2["intProperty"], a.intProperty)
        doAssertEquals(map2["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map2["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map2["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map2["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map2["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map2["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map2["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map2["aa"] as Aa).intString, "110")

        //map to bean
        val newB = B()
        BeanHelper.copyPropertiesIgnoreNull(map, newB)
        doAssertEquals(newB.stringProperty, "B.stringProperty")
        doAssertEquals(newB.intProperty, a.intProperty)
        doAssertEquals(newB.intArray, arrayOf("1", "2", "3"))
        doAssertEquals(newB.stringArray, arrayOf(1, 2, 3))
        doAssertEquals(newB.stringList, listOf(1, 2, 3))
        doAssertEquals(newB.stringSet, setOf(1, 2, 3))
        doAssertEquals(newB.stringMap, mapOf(1 to 1, 2 to 2, 3 to 3))
        doAssertEquals(newB.listMap, mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5)))
        doAssertEquals(newB.listMap2, mapOf(1 to listOf(10, 20, 30), 2 to listOf(20, 30, 40), 3 to listOf(30, 40, 50)))
        doAssertEquals(b.aa?.intString, 110)
    }

    @Test
    fun testPopulateProperties() {
        // bean to bean
        val a = A()
        val b = B()
        b.clear()

        //bean to map
        val map = mutableMapOf<String, Object>()
        BeanHelper.populateProperties(a, map)
        doAssertEquals(map["stringProperty"], a.stringProperty)
        doAssertEquals(map["intProperty"], a.intProperty)
        doAssertEquals(map["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map["aa"] as Aa).intString, "110")

        //map to map
        val map2 = mutableMapOf<String, Object>()
        BeanHelper.populateProperties(map, map2)
        doAssertEquals(map2["stringProperty"], a.stringProperty)
        doAssertEquals(map2["intProperty"], a.intProperty)
        doAssertEquals(map2["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map2["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map2["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map2["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map2["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map2["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map2["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map2["aa"] as Aa).intString, "110")
    }

    @Test
    fun testPopulatePropertiesIgnoreNull() {
        // bean to bean
        val a = A()
        val b = B()
        a.stringProperty = null

        //bean to map
        val map = mutableMapOf<String, Object>()
        BeanHelper.populatePropertiesIgnoreNull(a, map)
        doAssertEquals(map["stringProperty"], null)
        doAssertEquals(map["intProperty"], a.intProperty)
        doAssertEquals(map["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map["aa"] as Aa).intString, "110")

        //map to map
        val map2 = mutableMapOf<String, Object>()
        BeanHelper.populatePropertiesIgnoreNull(map, map2)
        doAssertEquals(map2["stringProperty"], null)
        doAssertEquals(map2["intProperty"], a.intProperty)
        doAssertEquals(map2["intArray"], arrayOf(1, 2, 3))
        doAssertEquals(map2["stringArray"], arrayOf("1", "2", "3"))
        doAssertEquals(map2["stringList"], listOf("1", "2", "3"))
        doAssertEquals(map2["stringSet"], setOf("1", "2", "3"))
        doAssertEquals(map2["stringMap"], mapOf("1" to "1", "2" to "2", "3" to "3"))
        doAssertEquals(
            map2["listMap"],
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        )
        doAssertEquals(
            map2["listMap2"],
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        doAssertEquals((map2["aa"] as Aa).intString, "110")
    }

    private val customBeanOperator = BeanOperator.newBuilder()
        .setBeanConverter(BeanConverterTest.customBeanConverter)
        .setBeanResolver(BeanResolverTest.customBeanResolver)
        .build()

    @Test
    fun testCustomOperator() {
        val a = A()
        val map = mutableMapOf<String, Object>()
        customBeanOperator.copyProperties(a, map)
        doAssertEquals(map["1"], 9)
        val c = customBeanOperator.convert<String>("", String::class.java)
        doAssertEquals(c, "6")
    }

    @Test
    fun testEmptyOperator() {
        doExpectThrowable(IllegalStateException::class.java) {
            BeanClassSupport.newBuilder().build()
        }

        val emptyBeanOperator = BeanOperator.newBuilder()
            .setBeanConverter(BeanConverter.DEFAULT)
            .setBeanResolver(
                BeanResolver.newBuilder()
                    .addHandlers(
                        object : BeanResolverHandler {
                            override fun supportBean(beanClass: Class<*>): Boolean {
                                return true;
                            }

                            override fun resolve(beanClass: Class<*>): BeanClass {
                                return BeanClassSupport.newBuilder()
                                    .setType(Object::class.java)
                                    .build()
                            }
                        }
                    )
                    .build()
            )
            .build()
        val emptyBeanClass = emptyBeanOperator.resolve(Object::class.java);
        doAssertEquals(emptyBeanClass.type, Object::class.java)
    }

    class A {
        var stringProperty: String? = "A.stringProperty"
        var intProperty = 998
        var intArray: IntArray? = intArrayOf(1, 2, 3)
        var stringArray: Array<String>? = arrayOf("1", "2", "3")
        var stringList: List<String>? = listOf("1", "2", "3")
        var stringSet: Set<String>? = setOf("1", "2", "3")
        var stringMap: Map<String, String>? = mapOf("1" to "1", "2" to "2", "3" to "3")
        var listMap: Map<String, List<String>>? =
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        var listMap2: Map<in String, List<out String>>? =
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        var aa: Aa? = Aa()

        fun clear() {
            stringProperty = null
            intArray = null
            stringArray = null
            stringList = null
            stringSet = null
            stringMap = null
            listMap = null
            listMap2 = null
            aa = null
        }
    }

    class Aa {
        var intString: String? = "110"
    }

    class B {
        var stringProperty: String? = "B.stringProperty"
        var intProperty = 1998
        var intArray: Array<String>? = arrayOf("1", "2", "3")
        var stringArray: Array<Int>? = arrayOf(1, 2, 3)
        var stringList: List<Int>? = listOf(1, 2, 3)
        var stringSet: Set<Int>? = setOf(1, 2, 3)
        var stringMap: Map<Int, Int>? = mapOf(1 to 1, 2 to 2, 3 to 3)
        var listMap: Map<Int, List<Int>>? =
            mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5))
        var listMap2: Map<out Int, List<out Int>>? =
            mapOf(1 to listOf(10, 20, 30), 2 to listOf(20, 30, 40), 3 to listOf(30, 40, 50))
        var aa: Bb? = Bb()

        fun clear() {
            stringProperty = null
            intArray = null
            stringArray = null
            stringList = null
            stringSet = null
            stringMap = null
            listMap = null
            listMap2 = null
            aa = null
        }
    }

    class Bb {
        var intString: Int? = 0
    }
}
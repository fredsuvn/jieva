package test.xyz.srclab.common.bean

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.model.ComplexModel
import test.xyz.srclab.common.model.SomeClass1
import test.xyz.srclab.common.model.SomeClass2
import xyz.srclab.common.bean.*
import java.lang.reflect.Type

object BeanOperatorTest {

    private val commonOperator = DefaultBeanOperator.DEFAULT

    @Test
    fun testSimpleCopyProperties() {
        val some1 = SomeClass1()
        some1.parentInt = 1
        some1.parentString = "some1.parentString"
        some1.some1String = "some1.some1String"
        val some2 = SomeClass2()
        some2.parentInt = 1
        some2.parentString = "some2.parentString"
        some2.some2String = "some2.some2String"
        commonOperator.copyProperties(some1, some2)
        println("some1.parentString: ${some1.parentString}")
        println("some2.parentString: ${some2.parentString}")
        doAssertEquals(some2.parentString, some1.parentString)

        some1.some1Some = some2
        commonOperator.copyProperties(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        doAssertEquals(some2.some1Some?.parentString, "some1.parentString")

        some1.some1Some = null
        commonOperator.copyPropertiesIgnoreNull(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        doAssertEquals(some2.some1Some?.parentString, "some1.parentString")
        commonOperator.copyProperties(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        doAssertEquals(some2.some1Some, null)

        val map1 = mutableMapOf<Any, Any>()
        commonOperator.copyProperties(some1, map1)
        println(map1)
        doAssertEquals(map1["parentString"], "some1.parentString")
        map1["parentString"] = "sssss"
        commonOperator.copyProperties(some1, map1)
        doAssertEquals(map1["parentString"], some1.parentString)
        doAssertEquals(map1["some1String"], some1.some1String)
    }

    private val customResolver = BeanResolver.newBuilder()
        .addHandler(object : BeanResolverHandler {

            override fun supportBean(bean: Any): Boolean {
                return true
            }

            override fun resolve(bean: Any): BeanClass {
                return BeanClassSupport.newBuilder()
                    .setType(bean.javaClass)
                    .setProperties(
                        mapOf(
                            "hello" to CustomBeanProperty(
                                "hello",
                                String::class.java
                            ),
                            "world" to CustomBeanProperty(
                                "world",
                                String::class.java
                            )
                        )
                    )
                    .build()
            }

        })
        .build()

    private val customConverter = BeanConverter.newBuilder()
        .addHandler(object : BeanConverterHandler {
            override fun supportConvert(from: Any?, to: Type?, beanOperator: BeanOperator?): Boolean {
                return true
            }

            override fun <T : Any?> convert(from: Any?, to: Type?, beanOperator: BeanOperator?): T? {
                return "$from customConverter" as T
            }
        })
        .build()

    private val customOperatorBuilder = BeanOperator.newBuilder()
        .setBeanResolver(customResolver)
        .setBeanConverter(customConverter)

    private val customOperator = customOperatorBuilder.build()

    @Test
    fun testCustom() {
        val some1 = SomeClass1()
        some1.parentInt = 1
        some1.parentString = "some1.parentString"
        some1.some1String = "some1.some1String"
        val beanDescriptor = customOperator.beanResolver.resolve(some1)
        val hello = beanDescriptor.getProperty("hello")
        val helloValue = hello?.getValue(some1)
        println("helloValue: $helloValue")
        doAssertEquals(helloValue, "hello")
    }

    @Test
    fun testOperatorBuilder() {
        val newOperator = customOperatorBuilder.build();
        println("old operator: $customOperator, new operator: $newOperator")
        doAssertEquals(newOperator, customOperator)
    }

    @Test
    fun testCopyProperties() {
        // bean to bean
        val a = A()
        val b = B()
        b.clear()
        BeanHelper.copyProperties(a, b)
        doAssertEquals(b.listMap?.get(2), listOf(2, 3, 4))

//        //bean to map
//        val complexMap = mutableMapOf<Any, Any>()
//        commonOperator.copyProperties(complex, complexMap)
//        println(complexMap)
//        doAssertEquals(complexMap["string"], "complex")
//        doAssertEquals((complexMap["map"] as Map<*, *>)["3"], "3")
//        doAssertEquals((complexMap["mapGeneric"] as Map<*, *>)[1], 1)
//        complex.mapNest = mapOf("99" to mapOf(88 to 88))
//        commonOperator.copyProperties(complex, complexMap)
//        println(complexMap)
//        doAssertEquals(complexMap["mapNest"].toString(), mapOf(99L to mapOf(88 to 88)).toString())
//
//        //map to bean
//        commonOperator.copyProperties(complexMap, complex2)
//        println(complex2.mapNest)
//        doAssertEquals(complex2.mapNest.toString(), mapOf(99L to mapOf(88 to 88)).toString())
//
//        //map to map
//        val newMap = mutableMapOf<Any, Any>()
//        commonOperator.copyProperties(complexMap, newMap)
//        println(newMap)
//        doAssertEquals(newMap.toString(), complexMap.toString())
    }

    @Test
    fun testPopulateProperties() {
        val complex = ComplexModel()
        complex.string = "complex"
        complex.map = mapOf("3" to "3", "6" to "6")
        complex.mapGeneric = mapOf(1 to 1, 2 to 2)

        //bean to map
        val complexMap = mutableMapOf<Any, Any>()
        commonOperator.populateProperties(complex, complexMap)
        println(complexMap)
        doAssertEquals(complexMap["string"], "complex")
        doAssertEquals((complexMap["map"] as Map<*, *>)["3"], "3")
        doAssertEquals((complexMap["mapGeneric"] as Map<*, *>)[1], 1)
        complex.mapNest = mapOf("99" to mapOf(88 to 88))
        commonOperator.populateProperties(complex, complexMap)
        println(complexMap)
        doAssertEquals(complexMap["mapNest"].toString(), mapOf(99L to mapOf(88 to 88)).toString())

        //map to map
        val newMap = mutableMapOf<Any, Any>()
        commonOperator.populateProperties(complexMap, newMap)
        println(newMap)
        doAssertEquals(newMap.toString(), complexMap.toString())

        //ignore null
        complex.string = null
        commonOperator.populatePropertiesIgnoreNull(complex, complexMap)
        doAssertEquals(complexMap["string"], "complex")
        commonOperator.populateProperties(complex, complexMap)
        doAssertEquals(complexMap["string"], null)
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

        fun clear() {
            stringProperty = null
            intArray = null
            stringArray = null
            stringList = null
            stringSet = null
            stringMap = null
            listMap = null
        }
    }

    class B {
        var stringProperty: String? = "A.stringProperty"
        var intProperty = 998
        var intArray: Array<String>? = arrayOf("1", "2", "3")
        var stringArray: Array<Int>? = arrayOf(1, 2, 3)
        var stringList: List<Int>? = listOf(1, 2, 3)
        var stringSet: Set<Int>? = setOf(1, 2, 3)
        var stringMap: Map<Int, Int>? = mapOf(1 to 1, 2 to 2, 3 to 3)
        var listMap: Map<Int, List<Int>>? =
            mapOf(1 to listOf(1, 2, 3), 2 to listOf(2, 3, 4), 3 to listOf(3, 4, 5))

        fun clear() {
            stringProperty = null
            intArray = null
            stringArray = null
            stringList = null
            stringSet = null
            stringMap = null
            listMap = null
        }
    }
}
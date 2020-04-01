//package test.xyz.srclab.common.bean
//
//import org.testng.annotations.Test
//import test.xyz.srclab.common.doAssert
//import test.xyz.srclab.common.model.ComplexModel
//import test.xyz.srclab.common.model.ComplexModel2
//import test.xyz.srclab.common.model.SomeClass1
//import test.xyz.srclab.common.model.SomeClass2
//import xyz.srclab.common.bean.*
//import java.lang.reflect.Type
//
//object BeanOperatorTest {
//
//    private val commonOperator = DefaultBeanOperator.DEFAULT
//
//    @Test
//    fun testSimpleCopyProperties() {
//        val some1 = SomeClass1()
//        some1.parentInt = 1
//        some1.parentString = "some1.parentString"
//        some1.some1String = "some1.some1String"
//        val some2 = SomeClass2()
//        some2.parentInt = 1
//        some2.parentString = "some2.parentString"
//        some2.some2String = "some2.some2String"
//        commonOperator.copyProperties(some1, some2)
//        println("some1.parentString: ${some1.parentString}")
//        println("some2.parentString: ${some2.parentString}")
//        doAssert(some2.parentString, some1.parentString)
//
//        some1.some1Some = some2
//        commonOperator.copyProperties(some1, some2)
//        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
//        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
//        doAssert(some2.some1Some?.parentString, "some1.parentString")
//
//        some1.some1Some = null
//        commonOperator.copyPropertiesIgnoreNull(some1, some2)
//        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
//        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
//        doAssert(some2.some1Some?.parentString, "some1.parentString")
//        commonOperator.copyProperties(some1, some2)
//        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
//        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
//        doAssert(some2.some1Some, null)
//
//        val map1 = mutableMapOf<Any, Any>()
//        commonOperator.copyProperties(some1, map1)
//        println(map1)
//        doAssert(map1["parentString"], "some1.parentString")
//        map1["parentString"] = "sssss"
//        commonOperator.copyProperties(some1, map1)
//        doAssert(map1["parentString"], some1.parentString)
//        doAssert(map1["some1String"], some1.some1String)
//    }
//
//    private val customResolver = BeanResolver.newBuilder()
//        .addHandler(object : BeanResolverHandler {
//
//            override fun supportBean(bean: Any): Boolean {
//                return true
//            }
//
//            override fun resolve(bean: Any): BeanDescriptor {
//                return BeanDescriptor.newBuilder()
//                    .setType(bean.javaClass)
//                    .setProperties(
//                        mapOf(
//                            "hello" to CustomBeanPropertyDescriptor(
//                                "hello",
//                                String::class.java
//                            ),
//                            "world" to CustomBeanPropertyDescriptor(
//                                "world",
//                                String::class.java
//                            )
//                        )
//                    )
//                    .build()
//            }
//
//        })
//        .build()
//
//    private val customConverter = BeanConverter.newBuilder()
//        .addHandler(object : BeanConverterHandler {
//            override fun supportConvert(from: Any?, to: Type?, beanOperator: BeanOperator?): Boolean {
//                return true
//            }
//
//            override fun <T : Any?> convert(from: Any?, to: Type?, beanOperator: BeanOperator?): T? {
//                return "$from customConverter" as T
//            }
//        })
//        .build()
//
//    private val customOperatorBuilder = BeanOperator.newBuilder()
//        .setBeanResolver(customResolver)
//        .setBeanConverter(customConverter)
//
//    private val customOperator = customOperatorBuilder.build()
//
//    @Test
//    fun testCustom() {
//        val some1 = SomeClass1()
//        some1.parentInt = 1
//        some1.parentString = "some1.parentString"
//        some1.some1String = "some1.some1String"
//        val beanDescriptor = customOperator.beanResolver.resolve(some1)
//        val hello = beanDescriptor.getPropertyDescriptor("hello")
//        val helloValue = hello?.getValue(some1)
//        println("helloValue: $helloValue")
//        doAssert(helloValue, "hello")
//    }
//
//    @Test
//    fun testOperatorBuilder() {
//        val newOperator = customOperatorBuilder.build();
//        println("old operator: $customOperator, new operator: $newOperator")
//        doAssert(newOperator, customOperator)
//    }
//
//    @Test
//    fun testCopyProperties() {
//        // bean to bean
//        val complex = ComplexModel()
//        complex.string = "complex"
//        complex.map = mapOf("3" to "3", "6" to "6")
//        complex.mapGeneric = mapOf(1 to 1, 2 to 2)
//        val complex2 = ComplexModel2()
//        complex2.mapNest = mapOf(1L to mapOf())
//        commonOperator.copyProperties(complex, complex2)
//        doAssert(complex2.string, "complex")
//        println(complex2.map)
//        doAssert(complex2.map[3], 3L)
//        println(complex2.mapGeneric)
//        doAssert(complex2.mapGeneric[1], "1")
//        doAssert(complex2.mapNest, mapOf<Any, Any>())
//        complex2.mapNest = mapOf(1L to mapOf())
//        commonOperator.copyPropertiesIgnoreNull(complex, complex2)
//        doAssert(complex2.mapNest, mapOf(1L to mapOf<Any, Any>()))
//
//        //bean to map
//        val complexMap = mutableMapOf<Any, Any>()
//        commonOperator.copyProperties(complex, complexMap)
//        println(complexMap)
//        doAssert(complexMap["string"], "complex")
//        doAssert((complexMap["map"] as Map<*, *>)["3"], "3")
//        doAssert((complexMap["mapGeneric"] as Map<*, *>)[1], 1)
//        complex.mapNest = mapOf("99" to mapOf(88 to 88))
//        commonOperator.copyProperties(complex, complexMap)
//        println(complexMap)
//        doAssert(complexMap["mapNest"].toString(), mapOf(99L to mapOf(88 to 88)).toString())
//
//        //map to bean
//        commonOperator.copyProperties(complexMap, complex2)
//        println(complex2.mapNest)
//        doAssert(complex2.mapNest.toString(), mapOf(99L to mapOf(88 to 88)).toString())
//
//        //map to map
//        val newMap = mutableMapOf<Any, Any>()
//        commonOperator.copyProperties(complexMap, newMap)
//        println(newMap)
//        doAssert(newMap.toString(), complexMap.toString())
//    }
//
//    @Test
//    fun testPopulateProperties() {
//        val complex = ComplexModel()
//        complex.string = "complex"
//        complex.map = mapOf("3" to "3", "6" to "6")
//        complex.mapGeneric = mapOf(1 to 1, 2 to 2)
//
//        //bean to map
//        val complexMap = mutableMapOf<Any, Any>()
//        commonOperator.populateProperties(complex, complexMap)
//        println(complexMap)
//        doAssert(complexMap["string"], "complex")
//        doAssert((complexMap["map"] as Map<*, *>)["3"], "3")
//        doAssert((complexMap["mapGeneric"] as Map<*, *>)[1], 1)
//        complex.mapNest = mapOf("99" to mapOf(88 to 88))
//        commonOperator.populateProperties(complex, complexMap)
//        println(complexMap)
//        doAssert(complexMap["mapNest"].toString(), mapOf(99L to mapOf(88 to 88)).toString())
//
//        //map to map
//        val newMap = mutableMapOf<Any, Any>()
//        commonOperator.populateProperties(complexMap, newMap)
//        println(newMap)
//        doAssert(newMap.toString(), complexMap.toString())
//
//        //ignore null
//        complex.string = null
//        commonOperator.populatePropertiesIgnoreNull(complex, complexMap)
//        doAssert(complexMap["string"], "complex")
//        commonOperator.populateProperties(complex, complexMap)
//        doAssert(complexMap["string"], null)
//    }
//}
package test.xyz.srclab.common.bean

import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.model.SomeClass1
import test.xyz.srclab.common.model.SomeClass1Clone
import xyz.srclab.common.bean.*
import xyz.srclab.common.reflect.ReflectHelper

object BeanConverterTest {

    private val commonConverter = CommonBeanConverter.getInstance()

    private val customConverter = BeanConverter.newBuilder()
        .addHandler(CommonBeanConverterHandler.getInstance())
        .addHandler(object : BeanConverterHandler {
            override fun supportConvert(from: Any?, to: Class<*>, beanOperator: BeanOperator): Boolean {
                if (ReflectHelper.isAssignable(to, Number::class.java)
                    || ReflectHelper.isAssignable(to, String::class.java)
                ) {
                    return true
                }
                return false;
            }

            override fun <T : Any?> convert(from: Any?, to: Class<T>, beanOperator: BeanOperator): T {
                if (ReflectHelper.isAssignable(to, Number::class.java)) {
                    return 999 as T
                }
                return "9999" as T
            }
        })
        .build()

    @Test(dataProvider = "commonDataProvider")
    fun testCommon(from: Any, to: Class<*>, expected: Any) {
        doTest(commonConverter, from, to, expected)
    }

    @Test(dataProvider = "customDataProvider")
    fun testCustom(from: Any, to: Class<*>, expected: Any) {
        doTest(customConverter, from, to, expected)
    }

    private fun doTest(converter: BeanConverter, from: Any, to: Class<*>, expected: Any) {
        val actual = converter.convert(from, to)
        println(
            "from: $from (type ${from::class.java}), " +
                    "to: $to, actual: $actual (type ${actual?.javaClass}), " +
                    "expected: $expected (type ${expected::class.java})"
        )
        Assert.assertEquals(actual, expected)
    }

    @DataProvider
    fun commonDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(1, String::class.java, "1"),
            arrayOf("1", Int::class.java, 1),
            arrayOf(666, Int::class.java, 666)
        )
    }

    @DataProvider
    fun customDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(1, String::class.java, "9999"),
            arrayOf("1", Int::class.java, 999),
            arrayOf(666, Int::class.java, 999)
        )
    }

    @Test
    fun testComplex() {
        val some1 = SomeClass1()
        some1.some1String = "some1String"
        some1.some1Int = 123
        val some1Clone = commonConverter.convert(some1, SomeClass1Clone::class.java)
        println(
            "from: $some1 (type ${some1::class.java}), " +
                    "to: ${SomeClass1Clone::class.java}, " +
                    "actual: $some1Clone (type ${some1Clone?.javaClass}), " +
                    "expected: $some1Clone (type ${some1Clone?.javaClass})"
        )
        println("some1.some1String = ${some1.some1String}, some1.some1Int = ${some1.some1Int}")
        println("some1Clone.some1String = ${some1Clone?.some1String}, some1Clone.some1Int = ${some1Clone?.some1Int}")
        Assert.assertEquals(some1Clone?.some1String, some1.some1String)
        Assert.assertEquals(some1Clone?.some1Int, some1.some1Int)
    }
}
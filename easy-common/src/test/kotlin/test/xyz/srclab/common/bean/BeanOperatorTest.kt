package test.xyz.srclab.common.bean

import org.testng.Assert
import org.testng.annotations.Test
import test.xyz.srclab.common.model.SomeClass1
import test.xyz.srclab.common.model.SomeClass2
import xyz.srclab.common.bean.*

object BeanOperatorTest {

    private val commonOperator = CommonBeanOperator.getInstance()
    private val commonOperatorIgnoreNull = CommonBeanOperatorIgnoreNull.getInstance()

    @Test
    fun testCommonCopyProperties() {
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
        Assert.assertEquals(some2.parentString, some1.parentString)

        some1.some1Some = some2
        commonOperator.copyProperties(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        Assert.assertEquals(some2.some1Some?.parentString, "some1.parentString")

        some1.some1Some = null
        commonOperatorIgnoreNull.copyProperties(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        Assert.assertEquals(some2.some1Some?.parentString, "some1.parentString")
        commonOperator.copyProperties(some1, some2)
        println("some1.some1Some?.parentString: ${some1.some1Some?.parentString}")
        println("some2.some1Some?.parentString: ${some2.some1Some?.parentString}")
        Assert.assertEquals(some2.some1Some, null)
    }

    private val customResolver = BeanResolver.newBuilder()
        .addHandler(object : BeanResolverHandler {

            override fun supportBean(bean: Any, beanOperator: BeanOperator): Boolean {
                return true
            }

            override fun resolve(bean: Any, beanOperator: BeanOperator): BeanDescriptor {
                return BeanDescriptor.newBuilder()
                    .setType(bean.javaClass)
                    .setProperties(
                        mapOf(
                            "hello" to CustomBeanPropertyDescriptor(
                                "hello",
                                String::class.java,
                                beanOperator.convert("hello", String::class.java)
                            ),
                            "world" to CustomBeanPropertyDescriptor(
                                "world",
                                String::class.java,
                                beanOperator.convert("world", String::class.java)
                            )
                        )
                    )
                    .build()
            }

        })
        .build()

    private val customConverter = BeanConverter.newBuilder()
        .addHandler(object : BeanConverterHandler {
            override fun supportConvert(from: Any?, to: Class<*>?, beanOperator: BeanOperator?): Boolean {
                return true
            }

            override fun <T : Any?> convert(from: Any?, to: Class<T>?, beanOperator: BeanOperator?): T? {
                return "$from customConverter" as T
            }
        })
        .build()

    private val customOperator = BeanOperator.newBuilder()
        .setBeanResolver(customResolver)
        .setBeanConverter(customConverter)
        .build()

    @Test
    fun testCustom() {
        val some1 = SomeClass1()
        some1.parentInt = 1
        some1.parentString = "some1.parentString"
        some1.some1String = "some1.some1String"
        val beanDescriptor = customOperator.beanResolver.resolve(some1)
        val hello = beanDescriptor.getPropertyDescriptor("hello")
        val helloValue = hello?.getValue(some1)
        println("helloValue: $helloValue")
        Assert.assertEquals(helloValue, "hello customConverter")
    }
}
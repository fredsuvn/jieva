package test.xyz.srclab.common.bytecode

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssert
import test.xyz.srclab.common.model.SomeSomeClass1
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.common.bytecode.bean.BeanClass

object BeanTest {

    @Test
    fun testBean() {
        val beanClass = BeanClass.newBuilder(SomeSomeClass1::class.java)
            .addProperty("hello", String::class.java)
            .addProperty("world", String::class.java)
            .build()
        val bean = beanClass.newInstance()
        val beanDescriptor = BeanHelper.resolve(bean)
        val helloProperty = beanDescriptor?.getPropertyDescriptor("hello")
        val worldProperty = beanDescriptor?.getPropertyDescriptor("world")
        doAssert(helloProperty?.name, "hello")
        doAssert(worldProperty?.name, "world")
    }
}
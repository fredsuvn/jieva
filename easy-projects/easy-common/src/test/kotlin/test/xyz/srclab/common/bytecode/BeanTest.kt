package test.xyz.srclab.common.bytecode

import org.testng.annotations.Test
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.common.bytecode.BeanClass
import xyz.srclab.common.bytecode.provider.ByteCodeProvider
import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider
import xyz.srclab.common.bytecode.provider.spring.SpringByteCodeProvider
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object BeanTest {

    @Test
    fun testBean() {
        val beanClass = BeanClass.newBuilder(A1::class.java)
            .addProperty("b", Int::class.java)
            .build()
        val instance = beanClass.newInstance()
        BeanHelper.setPropertyValue(instance, "b", 110)
        doAssertEquals(BeanHelper.getPropertyValue(instance, "b"), 110)

        val beanClass2 = BeanClass.newBuilder()
            .addProperty("b", Int::class.java)
            .build()
        val instance2 = beanClass2.newInstance()
        BeanHelper.setPropertyValue(instance2, "b", 110)
        doAssertEquals(BeanHelper.getPropertyValue(instance2, "b"), 110)
    }

    @Test
    fun testWithProvider() {
        doTestBean(CglibByteCodeProvider.INSTANCE, A2::class.java)
        doTestBean(SpringByteCodeProvider.INSTANCE, A3::class.java)
    }

    private fun <T : Any> doTestBean(provider: ByteCodeProvider, baseClass: Class<T>) {
        val beanClass = provider.newBeanClassBuilder(baseClass)
            .addProperty("b", Int::class.java)
            .build()
        val instance = beanClass.newInstance()
        BeanHelper.setPropertyValue(instance, "b", 110)
        doAssertEquals(BeanHelper.getPropertyValue(instance, "b"), 110)

        val beanClass2 = BeanClass.newBuilder()
            .addProperty("b", Int::class.java)
            .build()
        val instance2 = beanClass2.newInstance()
        BeanHelper.setPropertyValue(instance2, "b", 110)
        doAssertEquals(BeanHelper.getPropertyValue(instance2, "b"), 110)
    }

    open class A1 {
        var a = "a"
    }

    open class A2 {
        var a = "a"
    }

    open class A3 {
        var a = "a"
    }
}
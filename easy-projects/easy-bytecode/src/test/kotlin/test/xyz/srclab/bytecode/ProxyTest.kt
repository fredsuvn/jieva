package test.xyz.srclab.bytecode

import org.testng.annotations.Test
import xyz.srclab.bytecode.provider.ByteCodeProvider
import xyz.srclab.bytecode.provider.cglib.CglibByteCodeProvider
import xyz.srclab.bytecode.provider.cglib.SpringCglibByteCodeProvider
import xyz.srclab.bytecode.proxy.ProxyClass
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object ProxyTest {

    @Test
    fun testProxy() {
        val proxyClass = ProxyClass.newBuilder(A1::class.java)
            .addInterfaces(listOf(B::class.java))
            .overrideMethod(
                "hello", arrayOf()
            ) { `object`, args, method, superInvoker -> "proxy: " + superInvoker.invoke(`object`, args) }
            .overrideMethod(
                "bb", arrayOf()
            ) { `object`, args, method, superInvoker -> "proxy: bb" }
            .build()
        val instance = proxyClass.newInstance()
        doAssertEquals(instance.hello(), "proxy: world")
        doAssertEquals(
            BeanHelper.resolve(instance.javaClass).getMethod("bb").invoke(instance), "proxy: bb"
        )
    }

    @Test
    fun testWithProvider() {
        doTestProxy(CglibByteCodeProvider.INSTANCE, A3::class.java)
        doTestProxy(SpringCglibByteCodeProvider.INSTANCE, A2::class.java)
    }

    private fun <T : Any> doTestProxy(provider: ByteCodeProvider, baseClass: Class<T>) {
        val proxyClass = provider.newProxyClassBuilder(baseClass)
            .addInterfaces(listOf(B::class.java))
            .overrideMethod(
                "hello", arrayOf()
            ) { `object`, args, method, superInvoker -> "proxy: " + superInvoker.invoke(`object`, args) }
            .overrideMethod(
                "bb", arrayOf()
            ) { `object`, args, method, superInvoker -> "proxy: bb" }
            .build()
        val instance = proxyClass.newInstance()
        doAssertEquals(
            BeanHelper.resolve(instance.javaClass).getMethod("hello").invoke(instance),
            "proxy: world"
        )
        doAssertEquals(
            BeanHelper.resolve(instance.javaClass).getMethod("bb").invoke(instance),
            "proxy: bb"
        )
    }

    open class A1() {

        constructor(str: String) : this() {
            println(str)
        }

        open fun hello(): String {
            return "world"
        }
    }

    open class A2() {

        constructor(str: String) : this() {
            println(str)
        }

        open fun hello(): String {
            return "world"
        }
    }

    open class A3() {

        constructor(str: String) : this() {
            println(str)
        }

        open fun hello(): String {
            return "world"
        }
    }

    interface B {
        fun bb(): String
    }
}
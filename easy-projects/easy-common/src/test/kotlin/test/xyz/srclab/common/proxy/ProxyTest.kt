package test.xyz.srclab.common.proxy

import org.testng.annotations.Test
import xyz.srclab.common.util.proxy.ClassProxy
import xyz.srclab.common.util.proxy.provider.ClassProxyProvider
import xyz.srclab.common.util.proxy.provider.bytecode.ByteCodeClassProxyProvider
import xyz.srclab.common.util.proxy.provider.jdk.JdkClassProxyProvider
import xyz.srclab.common.invoke.MethodInvoker
import xyz.srclab.common.reflect.method.ProxyMethod
import xyz.srclab.test.doAssertEquals
import java.lang.reflect.Method

/**
 * @author sunqian
 */
object ProxyTest {

    @Test
    fun testProxy() {
        val classProxy = ClassProxy.newBuilder(A::class.java)
            .proxyMethod("hello", arrayOf(), object : ProxyMethod {
                override fun invoke(`object`: Any, args: Array<Any>, method: Method, superInvoker: MethodInvoker): Any {
                    return "proxy hello"
                }
            })
            .build()
        val a = classProxy.newInstance()
        doAssertEquals(a.hello(), "proxy hello")
    }

    @Test
    fun testWithProvider() {
        doTestProxy(
            JdkClassProxyProvider.INSTANCE,
            A::class.java
        )
        doTestProxy(
            ByteCodeClassProxyProvider.INSTANCE,
            A::class.java
        )
    }

    private fun <T : A> doTestProxy(provider: ClassProxyProvider, baseClass: Class<T>) {
        val classProxy = provider.newBuilder(baseClass)
            .proxyMethod("hello", arrayOf(), object : ProxyMethod {
                override fun invoke(`object`: Any, args: Array<Any>, method: Method, superInvoker: MethodInvoker): Any {
                    return "proxy hello"
                }
            })
            .build()
        val a = classProxy.newInstance()
        doAssertEquals(a.hello(), "proxy hello")
    }

    @Test
    fun testMultiParameterConstructor() {
        doTestMultiParameterConstructor(
            ByteCodeClassProxyProvider.INSTANCE, B::class.java, arrayOf(Int::class.java), arrayOf(222)
        )
    }


    private fun doTestMultiParameterConstructor(
        provider: ClassProxyProvider, baseClass: Class<B>,
        ParameterTypes: Array<Class<*>>, args: Array<Any>
    ) {
        val classProxy = provider.newBuilder(baseClass)
            .build()
        val b1 = classProxy.newInstance()
        doAssertEquals(b1.i, 0)
        val b2 = classProxy.newInstance(ParameterTypes, args)
        doAssertEquals(b2.i, 222)
    }

    interface A {
        fun hello(): String
    }

    open class B(val i: Int) {

        constructor() : this(0);
    }
}
package test.xyz.scrlab.common.proxy

import org.testng.annotations.Test
import xyz.srclab.common.proxy.ClassProxy
import xyz.srclab.common.proxy.provider.ClassProxyProvider
import xyz.srclab.common.proxy.provider.bytecode.ByteCodeClassProxyProvider
import xyz.srclab.common.proxy.provider.jdk.JdkClassProxyProvider
import xyz.srclab.common.reflect.invoke.MethodInvoker
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
        doTestProxy(JdkClassProxyProvider.INSTANCE, A::class.java)
        doTestProxy(ByteCodeClassProxyProvider.INSTANCE, A::class.java)
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

    interface A {
        fun hello(): String
    }
}
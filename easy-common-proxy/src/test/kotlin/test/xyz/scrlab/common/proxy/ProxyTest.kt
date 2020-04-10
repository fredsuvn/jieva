package test.xyz.scrlab.common.proxy

import org.testng.annotations.Test
import xyz.srclab.common.proxy.ClassProxy
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
                    return "proxy: " + superInvoker.invoke(`object`, args)
                }
            })
            .build()
        val a = classProxy.newInstance()
        doAssertEquals(a.hello(), "proxy: hello")
    }

    interface A {
        fun hello(): String {
            return "hello"
        }
    }
}
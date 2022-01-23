package sample.kotlin.xyz.srclab.core.proxy

import org.testng.annotations.Test
import xyz.srclab.common.proxy.ClassProxy
import xyz.srclab.common.proxy.ProxyMethod
import java.lang.reflect.Method
import java.util.*

class ProxySample {

    @Test
    fun testProxy() {
        val proxyClass = ClassProxy.newProxyClass(
            Any::class.java,
            listOf(
                object : ProxyMethod<Any> {

                    override fun isProxy(method: Method): Boolean {
                        return method.name == "toString" && Arrays.equals(
                            method.parameterTypes,
                            arrayOfNulls<Class<*>>(0)
                        )
                    }

                    override fun invoke(
                        proxied: Any,
                        proxiedMethod: Method,
                        sourceInvoke: SourceInvoke,
                        args: Array<out Any?>
                    ): Any {
                        return "Proxy[super: " + sourceInvoke.invoke(*args) + "]"
                    }
                }
            )
        )
        val s = proxyClass.newInstance().toString()
        //Proxy[super: net.sf.cglib.empty.Object$$EnhancerByCGLIB$$4926690c@256f38d9]
        logger.log("s: {}", s)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
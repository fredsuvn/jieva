package sample.kotlin.xyz.srclab.core.proxy

import org.testng.annotations.Test
import xyz.srclab.common.proxy.ProxyClass.Companion.newProxyClass
import xyz.srclab.common.proxy.ProxyMethod
import xyz.srclab.common.proxy.SuperInvoker
import xyz.srclab.common.test.TestLogger
import java.lang.reflect.Method

class ProxySample {

    @Test
    fun testProxy() {
        val proxyClass = newProxyClass(
            Any::class.java,
            listOf(
                object : ProxyMethod<Any> {
                    override val name: String
                        get() {
                            return "toString"
                        }

                    override val parameterTypes: Array<Class<*>>
                        get() {
                            return emptyArray()
                        }

                    override fun invoke(
                        proxied: Any,
                        proxiedMethod: Method,
                        args: Array<out Any?>?, superInvoker: SuperInvoker
                    ): Any? {
                        return "Proxy[super: " + superInvoker.invoke(args) + "]"
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
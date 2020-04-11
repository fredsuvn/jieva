package test.xyz.srclab.common.reflect

import org.testng.annotations.Test
import xyz.srclab.common.reflect.invoke.InvokerHelper
import xyz.srclab.common.reflect.invoke.InvokerProvider
import xyz.srclab.common.reflect.invoke.provider.methodhandle.MethodHandleInvokerProvider
import xyz.srclab.common.reflect.invoke.provider.reflected.ReflectedInvokerProvider
import xyz.srclab.test.doAssertEquals

object InvokerHelperTest {

    @Test
    fun testMethodInvoker() {
        val a = A()
        val method = A::class.java.getMethod("aaa")
        doAssertEquals(
            InvokerHelper.getMethodInvoker(A::class.java, "aaa").invoke(a),
            method.invoke(a)
        )
        doAssertEquals(
            InvokerHelper.getConstructorInvoker(A::class.java, String::class.java).invoke("cc"),
            A("cc")
        )
        doTestMethodInvoker(ReflectedInvokerProvider.INSTANCE)
        doTestMethodInvoker(MethodHandleInvokerProvider.INSTANCE)
    }

    private fun doTestMethodInvoker(invokerProvider: InvokerProvider) {
        val a = A()
        val method = A::class.java.getMethod("aaa")
        doAssertEquals(invokerProvider.newMethodInvoker(method).invoke(a), method.invoke(a))
        doAssertEquals(invokerProvider.newMethodInvoker(method).invoke(a), method.invoke(a))
    }

    class A() {

        var code: String? = null

        constructor(code: String) : this() {
            this.code = code
        }

        fun aaa(): String {
            return "aaa"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as A

            if (code != other.code) return false

            return true
        }

        override fun hashCode(): Int {
            return code?.hashCode() ?: 0
        }
    }
}
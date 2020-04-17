package test.xyz.srclab.common.reflect

import org.testng.annotations.Test
import xyz.srclab.common.invoke.InvokerHelper
import xyz.srclab.common.invoke.InvokerProvider
import xyz.srclab.common.invoke.provider.methodhandle.MethodHandleInvokerProvider
import xyz.srclab.common.invoke.provider.reflected.ReflectedInvokerProvider
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
        doTestStaticMethodInvoker(ReflectedInvokerProvider.INSTANCE)
        doTestStaticMethodInvoker(MethodHandleInvokerProvider.INSTANCE)
    }

    private fun doTestMethodInvoker(invokerProvider: InvokerProvider) {
        val a = A()
        val method = A::class.java.getMethod("aaa")
        val method1 = A::class.java.getMethod("aaa1", Int::class.java)
        val method2 = A::class.java.getMethod("aaa2", Int::class.java, Int::class.java)
        val method3 = A::class.java.getMethod("aaa3", Int::class.java, Int::class.java, Int::class.java)
        val method4 =
            A::class.java.getMethod("aaa4", Int::class.java, Int::class.java, Int::class.java, Int::class.java)
        val method5 = A::class.java.getMethod(
            "aaa5",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        val method6 = A::class.java.getMethod(
            "aaa6",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        doAssertEquals(invokerProvider.getMethodInvoker(method).invoke(a), method.invoke(a))
        doAssertEquals(
            invokerProvider.getMethodInvoker(method1).invoke(a, 1),
            method1.invoke(a, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method2).invoke(a, 1, 1),
            method2.invoke(a, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method3).invoke(a, 1, 1, 1),
            method3.invoke(a, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method4).invoke(a, 1, 1, 1, 1),
            method4.invoke(a, 1, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method5).invoke(a, 1, 1, 1, 1, 1),
            method5.invoke(a, 1, 1, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method6).invoke(a, 1, 1, 1, 1, 1, 1),
            method6.invoke(a, 1, 1, 1, 1, 1, 1)
        )
    }

    private fun doTestStaticMethodInvoker(invokerProvider: InvokerProvider) {
        val a = A()
        val method = A::class.java.getMethod("saaa")
        val method1 = A::class.java.getMethod("saaa1", Int::class.java)
        val method2 = A::class.java.getMethod("saaa2", Int::class.java, Int::class.java)
        val method3 = A::class.java.getMethod("saaa3", Int::class.java, Int::class.java, Int::class.java)
        val method4 =
            A::class.java.getMethod("saaa4", Int::class.java, Int::class.java, Int::class.java, Int::class.java)
        val method5 = A::class.java.getMethod(
            "saaa5",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        val method6 = A::class.java.getMethod(
            "saaa6",
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
        doAssertEquals(invokerProvider.getMethodInvoker(method).invoke(a), method.invoke(a))
        doAssertEquals(
            invokerProvider.getMethodInvoker(method1).invoke(a, 1),
            method1.invoke(a, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method2).invoke(a, 1, 1),
            method2.invoke(a, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method3).invoke(a, 1, 1, 1),
            method3.invoke(a, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method4).invoke(a, 1, 1, 1, 1),
            method4.invoke(a, 1, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method5).invoke(a, 1, 1, 1, 1, 1),
            method5.invoke(a, 1, 1, 1, 1, 1)
        )
        doAssertEquals(
            invokerProvider.getMethodInvoker(method6).invoke(a, 1, 1, 1, 1, 1, 1),
            method6.invoke(a, 1, 1, 1, 1, 1, 1)
        )
    }

    class A() {

        var code: String? = null

        constructor(code: String) : this() {
            this.code = code
        }

        fun aaa(): String {
            return "aaa"
        }

        fun aaa1(arg0: Int): String {
            return "aaa1"
        }

        fun aaa2(arg0: Int, arg1: Int): String {
            return "aaa2"
        }

        fun aaa3(arg0: Int, arg1: Int, arg2: Int): String {
            return "aaa3"
        }

        fun aaa4(arg0: Int, arg1: Int, arg2: Int, arg3: Int): String {
            return "aaa4"
        }

        fun aaa5(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): String {
            return "aaa5"
        }

        fun aaa6(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): String {
            return "aaa6"
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

        companion object {
            @JvmStatic
            fun saaa(): String {
                return "aaa"
            }

            @JvmStatic
            fun saaa1(arg0: Int): String {
                return "aaa1"
            }

            @JvmStatic
            fun saaa2(arg0: Int, arg1: Int): String {
                return "aaa2"
            }

            @JvmStatic
            fun saaa3(arg0: Int, arg1: Int, arg2: Int): String {
                return "aaa3"
            }

            @JvmStatic
            fun saaa4(arg0: Int, arg1: Int, arg2: Int, arg3: Int): String {
                return "aaa4"
            }

            @JvmStatic
            fun saaa5(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): String {
                return "aaa5"
            }

            @JvmStatic
            fun saaa6(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): String {
                return "aaa6"
            }
        }
    }
}
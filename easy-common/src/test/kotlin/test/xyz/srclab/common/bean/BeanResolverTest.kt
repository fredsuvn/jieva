package test.xyz.srclab.common.bean

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.base.KeyHelper
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.common.reflect.SignatureHelper

object BeanResolverTest {

    @Test
    fun testResolve() {
        val a = A()
        val beanClass = BeanHelper.resolve(A::class.java)
        doAssertEquals(
            beanClass.getProperty("listMap2").getValue(a),
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))
        )
        val clear = beanClass.getMethod("clear", arrayOf())
        clear.invoke(a)
        doAssertEquals(
            beanClass.getProperty("listMap2").getValue(a), null
        )
        val someMethodInvoker = beanClass.getMethod(
            "someMethod",
            arrayOf(String::class.java, List::class.java)
        )
        val someMethodMethod = someMethodInvoker.method
        val someMethodInvokerResult = someMethodInvoker.invoke(a, "aa", listOf("l1", "l2"))
        val someMethodMethodResult = someMethodMethod.invoke(a, "aa", listOf("l1", "l2"))
        doAssertEquals(someMethodInvokerResult, "aa:[l1, l2]")
        doAssertEquals(someMethodMethodResult, "aa:[l1, l2]")
        val clear2 = beanClass.getMethodBySignature(SignatureHelper.signMethod("clear", arrayOf()))
        doAssertEquals(clear2, clear)
    }

    class A {
        var stringProperty: String? = "A.stringProperty"
        var intProperty = 998
        var intArray: IntArray? = intArrayOf(1, 2, 3)
        var stringArray: Array<String>? = arrayOf("1", "2", "3")
        var stringList: List<String>? = listOf("1", "2", "3")
        var stringSet: Set<String>? = setOf("1", "2", "3")
        var stringMap: Map<String, String>? = mapOf("1" to "1", "2" to "2", "3" to "3")
        var listMap: Map<String, List<String>>? =
            mapOf("1" to listOf("1", "2", "3"), "2" to listOf("2", "3", "4"), "3" to listOf("3", "4", "5"))
        var listMap2: Map<in String, List<out String>>? =
            mapOf("1" to listOf("10", "20", "30"), "2" to listOf("20", "30", "40"), "3" to listOf("30", "40", "50"))

        fun clear() {
            stringProperty = null
            intArray = null
            stringArray = null
            stringList = null
            stringSet = null
            stringMap = null
            listMap = null
            listMap2 = null
        }

        fun someMethod(a: String, b: List<out String>): String {
            return KeyHelper.buildKey(a, b).toString()
        }
    }
}
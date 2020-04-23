package test.xyz.srclab.common.reflect

import org.testng.annotations.Test
import xyz.srclab.common.reflect.signature.SignatureHelper
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable

object SignatureHelperTest {

    @Test
    fun testSignClass() {
        doAssertEquals(SignatureHelper.signClass(Void.TYPE), "V")
        doAssertEquals(SignatureHelper.signClass(true.javaClass), "Z")
        doAssertEquals(SignatureHelper.signClass(1.javaClass), "I")
        doAssertEquals(SignatureHelper.signClass(1L.javaClass), "J")
        doAssertEquals(SignatureHelper.signClass(1f.javaClass), "F")
        doAssertEquals(SignatureHelper.signClass(1.0.javaClass), "D")
        doAssertEquals(SignatureHelper.signClass(1.toByte().javaClass), "B")
        doAssertEquals(SignatureHelper.signClass(1.toChar().javaClass), "C")
        doAssertEquals(SignatureHelper.signClass(1.toShort().javaClass), "S")

        doAssertEquals(
            SignatureHelper.signClass(Any::class.java),
            "Ljava/lang/Object;"
        )
        doAssertEquals(
            SignatureHelper.signClass(Array<Any>::class.java),
            "[Ljava/lang/Object;"
        )

        doExpectThrowable(IllegalArgumentException::class.java) {
            SignatureHelper.signPrimitiveClass(Any::class.java)
        }
    }

    @Test
    fun testSignMethod() {
        doAssertEquals(
            SignatureHelper.signMethod(A::class.java.getMethod("with0")),
            "with0()"
        )
        doAssertEquals(
            SignatureHelper.signMethod(A::class.java.getMethod("with1", Int::class.java)),
            "with1(int)"
        )
        doAssertEquals(
            SignatureHelper.signMethod(A::class.java.getMethod("with2", Int::class.java, Int::class.java)),
            "with2(int,int)"
        )
    }

    interface A {
        fun with0(): Int
        fun with1(str: Int): Int
        fun with2(str1: Int, str2: Int): Int
    }
}
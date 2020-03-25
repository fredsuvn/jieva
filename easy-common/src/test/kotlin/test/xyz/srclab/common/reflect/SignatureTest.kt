package test.xyz.srclab.common.reflect

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.doAssert
import xyz.srclab.common.reflect.SignatureHelper

object SignatureTest {

    @Test(dataProvider = "classSignatureDataProvider")
    fun testClassSignature(actual: String, expected: String) {
        doAssert(actual, expected)
    }

    @DataProvider
    fun classSignatureDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(SignatureHelper.signature(Int::class.java), "I"),
            arrayOf(SignatureHelper.signature(Long::class.java), "J"),
            arrayOf(SignatureHelper.signature(String::class.java), "Ljava/lang/String;"),
            arrayOf(SignatureHelper.signature(arrayOf("1")::class.java), "[Ljava/lang/String;")
        )
    }

    private val getClassMethod = Object::class.java.getMethod("getClass")
    private val toStringMethod = Object::class.java.getMethod("toString")
    private val subSequenceMethod = String::class.java.getMethod("subSequence", Int::class.java, Int::class.java)

    @Test(dataProvider = "methodSignatureDataProvider")
    fun testMethodSignature(actual: String, expected: String) {
        doAssert(actual, expected)
    }

    @DataProvider
    fun methodSignatureDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(SignatureHelper.signatureMethodWithMethodName(getClassMethod), "MgetClass()Ljava/lang/Class;"),
            arrayOf(SignatureHelper.signatureMethodWithMethodName(toStringMethod), "MtoString()Ljava/lang/String;"),
            arrayOf(
                SignatureHelper.signatureMethodWithMethodName(subSequenceMethod),
                "MsubSequence(II)Ljava/lang/CharSequence;"
            ),
            arrayOf(
                SignatureHelper.signatureMethodWithClassAndMethodName(getClassMethod),
                "MLjava/lang/Object;.getClass()Ljava/lang/Class;"
            ),
            arrayOf(
                SignatureHelper.signatureMethodWithClassAndMethodName(toStringMethod),
                "MLjava/lang/Object;.toString()Ljava/lang/String;"
            ),
            arrayOf(
                SignatureHelper.signatureMethodWithClassAndMethodName(subSequenceMethod),
                "MLjava/lang/String;.subSequence(II)Ljava/lang/CharSequence;"
            )
        )
    }
}
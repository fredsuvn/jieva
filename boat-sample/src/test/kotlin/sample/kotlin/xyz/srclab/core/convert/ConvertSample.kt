package sample.kotlin.xyz.srclab.core.convert

import org.testng.annotations.Test
import xyz.srclab.common.convert.FastConvertHandler
import xyz.srclab.common.convert.FastConverter
import xyz.srclab.common.convert.convert
import xyz.srclab.common.test.TestLogger

class ConvertSample {

    @Test
    fun testConvert() {
        val s = 123.convert(String::class.java)
        //123
        logger.log("s: {}", s)
        val a = A()
        a.p1 = "1"
        a.p2 = "2"
        val b = a.convert(
            B::class.java
        )
        //1
        logger.log("b1: {}", b.p1)
        //2
        logger.log("b1: {}", b.p2)

        val fastConverter =
            FastConverter.newFastConverter(listOf(IntToStringConvertHandler, NumberToStringConvertHandler))
        //I123
        logger.log(fastConverter.convert(123, String::class.java))
        //N123
        logger.log(fastConverter.convert(123L, String::class.java))
    }


    companion object {
        private val logger = TestLogger.DEFAULT
    }
}

class A {
    var p1: String? = null
    var p2: String? = null
}

class B {
    var p1 = 0
    var p2 = 0
}

private object IntToStringConvertHandler : FastConvertHandler<Int, String> {
    override val fromType: Class<*> = Int::class.java
    override val toType: Class<*> = String::class.java
    override fun convert(from: Int): String {
        return "I$from"
    }
}

private object NumberToStringConvertHandler : FastConvertHandler<Number, String> {
    override val fromType: Class<*> = Number::class.java
    override val toType: Class<*> = String::class.java
    override fun convert(from: Number): String {
        return "N$from"
    }
}
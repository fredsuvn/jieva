package sample.kotlin.xyz.srclab.common.convert

import org.testng.annotations.Test
import xyz.srclab.common.convert.FastConvertHandler
import xyz.srclab.common.convert.FastConverter.Companion.newFastConverter
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

        val fastConverter = newFastConverter(listOf(ObjectConvertHandler(), StringConvertHandler()))
        //123
        //123
        logger.log(fastConverter.convert(StringBuilder("123")))
        //123123
        //123123
        logger.log(fastConverter.convert("123"))
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

private class ObjectConvertHandler : FastConvertHandler<String> {

    override val supportedType: Class<*> = Any::class.java

    override fun convert(from: Any): String {
        return from.toString()
    }
}

private class StringConvertHandler : FastConvertHandler<String> {

    override val supportedType: Class<*> = String::class.java

    override fun convert(from: Any): String {
        return from.toString() + from.toString()
    }
}
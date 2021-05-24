package sample.kotlin.xyz.srclab.core.reflect

import org.testng.annotations.Test
import xyz.srclab.common.reflect.genericArrayType
import xyz.srclab.common.reflect.invoke
import xyz.srclab.common.reflect.method
import xyz.srclab.common.reflect.parameterizedType
import xyz.srclab.common.test.TestLogger

class ReflectSample {

    @Test
    fun testReflects() {
        val method = Any::class.java.method("toString")
        val s = method.invoke<String>(Any())
        //java.lang.Object@97c879e
        logger.log("s: {}", s)
    }

    @Test
    fun testTypes() {
        val type = parameterizedType(MutableList::class.java, String::class.java)
        val arrayType = type.genericArrayType()
        //java.util.List<java.lang.String>[]
        logger.log("arrayType: {}", arrayType)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
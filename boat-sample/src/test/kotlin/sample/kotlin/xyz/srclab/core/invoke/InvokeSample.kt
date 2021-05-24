package sample.kotlin.xyz.srclab.core.invoke

import org.testng.annotations.Test
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.test.TestLogger

class InvokeSample {

    @Test
    fun testInvoke() {
        val invoker: Invoker = Invoker.forMethod(String::class.java, "getBytes")
        val bytes = invoker.invoke<ByteArray>("10086")
        //[49, 48, 48, 56, 54]
        logger.log("bytes: {}", bytes)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
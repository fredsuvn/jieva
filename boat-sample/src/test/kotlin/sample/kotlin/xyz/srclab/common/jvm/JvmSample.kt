package sample.kotlin.xyz.srclab.common.jvm

import org.testng.annotations.Test
import xyz.srclab.common.jvm.jvmDescriptor
import xyz.srclab.common.test.TestLogger

class JvmSample {

    @Test
    fun testJvms() {
        val jvmDescriptor = Int::class.javaPrimitiveType!!.jvmDescriptor
        //I
        logger.log("jvmDescriptor: {}", jvmDescriptor)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
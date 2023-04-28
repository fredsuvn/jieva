package sample.kotlin.xyz.srclab.core.jvm

import org.testng.annotations.Test
import xyz.srclab.common.jvm.jvmDescriptor

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
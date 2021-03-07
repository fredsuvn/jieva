package sample.kotlin.xyz.srclab.common.exception

import org.testng.annotations.Test
import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.exception.StatusException
import xyz.srclab.common.test.TestLogger

class ExceptionSample {

    @Test
    fun testStatusException() {
        val sampleException = SampleException()
        //000001-Unknown Error[for sample]
        logger.log("Status: {}", sampleException.withMoreDescription("for sample"))
    }

    class SampleException : StatusException(ExceptionStatus.UNKNOWN)

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
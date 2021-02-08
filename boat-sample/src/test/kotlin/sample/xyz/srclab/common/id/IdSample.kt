package sample.xyz.srclab.common.id

import org.testng.annotations.Test
import xyz.srclab.common.id.StringIdSpec
import xyz.srclab.common.test.TestLogger

class IdSampleKt {

    @Test
    fun testId() {
        val spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}"
        val stringIdSpec = StringIdSpec(spec)
        //seq-202102071449568890000-tail
        for (i in 0..9) {
            logger.log(stringIdSpec.newId())
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}
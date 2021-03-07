package sample.kotlin.xyz.srclab.annotations

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.annotations.Written

class AnnotationSample {

    @Test
    fun testAnnotations() {
        val buffer = StringBuilder()
        buffer.writeBuffer("123")
        Assert.assertEquals(buffer.toString(), "123")
    }

    private fun @receiver:Written StringBuilder.writeBuffer(
        @Acceptable(
            Accepted(String::class),
            Accepted(StringBuffer::class),
        )
        readOnly: String
    ) {
        this.append(readOnly)
    }
}
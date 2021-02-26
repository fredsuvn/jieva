package sample.xyz.srclab.annotations

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.annotations.Written

class AnnotationSampleKt {

    @Test
    fun testAnnotations() {
        val buffer = StringBuilder()
        buffer.writeBuffer("123")
        Assert.assertEquals(buffer.toString(), "123")
    }

    private fun @receiver:Written StringBuilder.writeBuffer(readOnly: String) {
        this.append(readOnly)
    }
}
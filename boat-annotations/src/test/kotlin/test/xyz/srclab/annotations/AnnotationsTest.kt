package test.xyz.srclab.annotations

import org.testng.annotations.Test
import xyz.srclab.annotations.*

class AnnotationsTestKt {

    @Test
    fun testAnnotations() {
    }
}

fun @Modifiable @Rejectable(
    Rejected(String::class),
    Rejected(String::class),
) Array<String>.testOutParam():
        @Acceptable(
            Accepted(String::class),
            Accepted(StringBuilder::class),
        )
        CharSequence {
    return ""
}
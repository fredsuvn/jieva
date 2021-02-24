package test.xyz.srclab.annotations

import org.testng.annotations.Test
import xyz.srclab.annotations.*
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.memberExtensionFunctions

class AnnotationsTestKt {

    @Test
    fun testAnnotations() {
        val method = AnnotationsTestKt::class.java.getMethod("testOutParam", Array<String>::class.java)
        println(method.parameters[0].declaredAnnotations.joinToString())
        println(method.annotations.joinToString())
        val fs = AnnotationsTestKt::class.memberExtensionFunctions
        for (f in fs) {
            println(f)
            println(f.extensionReceiverParameter!!.annotations.joinToString())
        }
    }

    @Acceptable(
        Accepted(String::class),
    )
    fun @Modifiable @Rejectable(
        Rejected(String::class),
        Rejected(String::class),
    ) Array<String>.testOutParam():
            @Acceptable(
                Accepted(String::class),
                Accepted(StringBuilder::class),
            )
            CharSequence {
        return "$this this"
    }
}
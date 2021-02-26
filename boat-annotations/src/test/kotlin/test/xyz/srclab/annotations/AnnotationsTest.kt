package test.xyz.srclab.annotations

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.annotations.*

class AnnotationsTestKt {

    @Test
    fun testAnnotations() {
        val method = this.javaClass.getMethod("testAnnotations", Array<String>::class.java, String::class.java)
        val methodAcceptable = method.getAnnotation(Acceptable::class.java)
        Assert.assertNotNull(methodAcceptable)
        Assert.assertEquals(methodAcceptable.value.size, 1)
        Assert.assertEquals(methodAcceptable.value[0].value.java, String::class.java)

        Assert.assertNotNull(method.parameters[0].getAnnotation(Written::class.java))
        val receiverRejectable = method.parameters[0].getAnnotation(Rejectable::class.java)
        Assert.assertNotNull(receiverRejectable)
        Assert.assertEquals(receiverRejectable.value.size, 2)
        Assert.assertEquals(receiverRejectable.value[0].value.java, String::class.java)
        Assert.assertEquals(receiverRejectable.value[1].value.java, String::class.java)

        val paramRejectable = method.parameters[0].getAnnotation(Rejectable::class.java)
        Assert.assertNotNull(paramRejectable)
        Assert.assertEquals(paramRejectable.value.size, 2)
        Assert.assertEquals(paramRejectable.value[0].value.java, String::class.java)
        Assert.assertEquals(paramRejectable.value[1].value.java, String::class.java)
    }

    @Acceptable(
        Accepted(String::class),
    )
    fun @receiver:Written @receiver:Rejectable(
        Rejected(String::class),
        Rejected(String::class),
    ) Array<String>.testAnnotations(
        @Rejectable(
            Rejected(String::class),
            Rejected(String::class),
        ) param: String
    ): CharSequence {
        return "$this this"
    }
}
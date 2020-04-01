package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.lang.Ref
import xyz.srclab.common.lang.TypeRef

object TypeRefTest {

    private fun forGeneric(): Ref<String> {
        return Ref.withEmpty()
    }

    @Test
    fun testTypeRef() {
        open class B : TypeRef<Ref<String>>()
        open class C : B()
        open class D : C()

        val generic = D().type
        println(generic)
        doAssertEquals(generic, TypeRefTest::class.java.getDeclaredMethod("forGeneric").genericReturnType)
    }
}

package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import xyz.srclab.common.lang.ref.Ref
import xyz.srclab.common.reflect.type.TypeRef
import xyz.srclab.test.doAssertEquals

object TypeRefTest {

    private fun forGeneric(): Ref<String> {
        return Ref.ofEmpty()
    }

    @Test
    fun testTypeRef() {
        open class B : TypeRef<Ref<String>>()
        open class C : B()
        open class D : C()

        val generic = D().type
        doAssertEquals(
            generic,
            TypeRefTest::class.java.getDeclaredMethod("forGeneric").genericReturnType
        )
    }
}

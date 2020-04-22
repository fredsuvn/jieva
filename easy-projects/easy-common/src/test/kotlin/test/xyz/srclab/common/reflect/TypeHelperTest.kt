package test.xyz.srclab.common.reflect

import org.testng.annotations.Test
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.common.reflect.type.TypeRef
import xyz.srclab.common.reflect.type.TypeHelper
import xyz.srclab.test.doAssertEquals
import java.lang.reflect.ParameterizedType

object TypeHelperTest {

    @Test
    fun testAssignable() {
        doAssertEquals(TypeHelper.isAssignable(Any(), Any::class.java), true)
        doAssertEquals(TypeHelper.isAssignable(Any::class.java, Any::class.java), true)
    }

    @Test
    fun testRawClass() {
        doAssertEquals(TypeHelper.getRawClass(A::class.java.typeParameters[0]), Throwable::class.java)
        doAssertEquals(TypeHelper.getRawClass(A2::class.java.typeParameters[0]), List::class.java)
        doAssertEquals(
            TypeHelper.getRawClass(BeanHelper.resolveBean(B::class.java).getProperty("array")?.genericType),
            Any::class.java
        )
    }

    @Test
    fun testSuperclassGeneric() {
        doAssertEquals(
            (TypeHelper.findSuperclassGeneric(
                C::class.java,
                TypeRef::class.java
            ) as ParameterizedType).actualTypeArguments[0],
            C().type
        )
        doAssertEquals(
            TypeHelper.findSuperclassGeneric(B::class.java, TypeRef::class.java),
            null
        )
    }

    open class A<T : Throwable>

    class A2<T : List<Throwable>>

    class B {
        var array: Array<A<NullPointerException>>? = null
        var array2: Array<A2<List<NullPointerException>>>? = null
    }

    class C : TypeRef<List<String>>()
}
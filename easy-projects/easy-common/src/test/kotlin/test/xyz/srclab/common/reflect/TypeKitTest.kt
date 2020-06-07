package test.xyz.srclab.common.reflect

import org.testng.annotations.Test
import xyz.srclab.common.bean.BeanKit
import xyz.srclab.common.reflect.TypeKit
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.test.doAssertEquals
import java.lang.reflect.ParameterizedType

object TypeKitTest {

    @Test
    fun testAssignable() {
        doAssertEquals(TypeKit.isAssignable(Any(), Any::class.java), true)
        doAssertEquals(TypeKit.isAssignable(Any::class.java, Any::class.java), true)
    }

    @Test
    fun testRawClass() {
        doAssertEquals(TypeKit.getRawType<Any>(A::class.java.typeParameters[0]), Throwable::class.java)
        doAssertEquals(TypeKit.getRawType<Any>(A2::class.java.typeParameters[0]), List::class.java)
        doAssertEquals(
            TypeKit.getRawType<Any>(BeanKit.resolveBean(B::class.java).getProperty("array")?.genericType()),
            Any::class.java
        )
    }

    @Test
    fun testSuperclassGeneric() {
        doAssertEquals(
            (TypeKit.getGenericSuperclass(
                C::class.java,
                TypeRef::class.java
            ) as ParameterizedType).actualTypeArguments[0],
            C().type
        )
        doAssertEquals(
            TypeKit.getGenericSuperclass(B::class.java, TypeRef::class.java),
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
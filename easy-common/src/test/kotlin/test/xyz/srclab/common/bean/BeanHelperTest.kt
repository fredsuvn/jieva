package test.xyz.srclab.common.bean

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable
import xyz.srclab.common.bean.BeanHelper
import xyz.srclab.common.bean.BeanMethodNotFoundException
import xyz.srclab.common.bean.BeanPropertyNotFoundException
import xyz.srclab.common.lang.TypeRef
import xyz.srclab.common.reflect.SignatureHelper
import java.lang.reflect.Type

/**
 * @author sunqian
 */
object BeanHelperTest {

    @Test
    fun testCopyProperties() {
        val a = A()
        a.property = "666"
        val b = A()
        BeanHelper.copyProperties(a, b) { _, sourcePropertyValue, _, destPropertySetter, _ ->
            destPropertySetter.set(sourcePropertyValue)
        }
        doAssertEquals(b.property, "666")
    }

    @Test
    fun testPopulateProperties() {
        val a = A()
        a.property = "666"
        val b = mutableMapOf<String, Any>()
        BeanHelper.populateProperties(a, b) { sourcePropertyName, sourcePropertyValue, keyValueSetter, _ ->
            keyValueSetter.set(sourcePropertyName.toString(), sourcePropertyValue)
        }
        doAssertEquals(b["property"], "666")
    }

    @Test
    fun testProperty() {
        val a = A()
        a.property = "666"
        val value = BeanHelper.getPropertyValue(a, "property")
        doAssertEquals(value, "666")
        val valueAsInt = BeanHelper.getPropertyValue(a, "property", Int::class.java)
        doAssertEquals(valueAsInt, 666)
        val valueAsIntType = BeanHelper.getPropertyValue(a, "property", Int::class.java as Type) as Int
        doAssertEquals(valueAsIntType, 666)
        val valueAsDouble =
            BeanHelper.getPropertyValue(a, "property", TypeRef.with(Double::class.java))
        doAssertEquals(valueAsDouble, 666.0)

        BeanHelper.setProperty(a, "property", null)
        val valueAsNull = BeanHelper.getPropertyValue(a, "property", Int::class.java)
        doAssertEquals(valueAsNull, null)
    }

    @Test
    fun testConvert() {
        doAssertEquals(BeanHelper.convert("1", Int::class.java), 1)
        doAssertEquals(BeanHelper.convert("1", Int::class.java as Type), 1)
        doAssertEquals(BeanHelper.convert("1", TypeRef.with(Int::class.java)), 1)
    }

    @Test
    fun testMethod() {
        val a = A()
        a.property = "666"
        val getter = BeanHelper.getMethod(a, "getProperty")
        doAssertEquals(getter.invoke(a), "666")
        val getterBySignature = BeanHelper.getMethodBySignature(a, "getProperty()")
        doAssertEquals(getterBySignature.invoke(a), "666")
    }

    @Test
    fun testNotFound() {
        doExpectThrowable(BeanPropertyNotFoundException::class.java) {
            BeanHelper.getProperty(Any(), "FFFF")
        }.catch { e ->
            doAssertEquals(e.propertyName, "FFFF")
        }
        doExpectThrowable(BeanMethodNotFoundException::class.java) {
            BeanHelper.getMethod(Any(), "FFFF")
        }.catch { e ->
            doAssertEquals(
                e.methodSignature,
                SignatureHelper.signMethod("FFFF", arrayOf())
            )
        }
        doExpectThrowable(BeanMethodNotFoundException::class.java) {
            BeanHelper.getMethodBySignature(Any(), "FFFF")
        }.catch { e ->
            doAssertEquals(e.methodSignature, "FFFF")
        }
    }

    @Test
    fun testClone() {
        val a = A()
        a.property = "aaa"
        val b = BeanHelper.clone(a)
        doAssertEquals(a !== b, true)
        doAssertEquals(b, a)
    }

    @Test
    fun testToMap() {
        val a = A()
        a.property = "aaa"
        val b = BeanHelper.toMap(a)
        doAssertEquals(b, mapOf("class" to A::class.java, "property" to "aaa"))
    }

    class A {
        var property: String? = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as A

            if (property != other.property) return false

            return true
        }

        override fun hashCode(): Int {
            return property?.hashCode() ?: 0
        }
    }
}
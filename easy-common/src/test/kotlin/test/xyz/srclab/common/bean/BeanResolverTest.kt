package test.xyz.srclab.common.bean

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.doExpectThrowable
import xyz.srclab.common.bean.*
import xyz.srclab.common.reflect.SignatureHelper
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.*

object BeanResolverTest {

    @Test
    fun testResolve() {
        val a = A()
        val beanClass = BeanHelper.resolve(A::class.java)

        val readProperty = beanClass.getProperty("read")
        doAssertEquals(beanClass.canReadProperty("read"), true)
        doAssertEquals(beanClass.canWriteProperty("read"), false)
        doAssertEquals(readProperty.get().getValue(a), a.read)
        doExpectThrowable(UnsupportedOperationException::class.java) {
            readProperty.get().setValue(a, "read2")
        }

        val wwwProperty = beanClass.getProperty("www")
        if (beanClass.canReadProperty("www")) {
            doAssertEquals(wwwProperty.get().getValue(a), a.www)
        }
        val xyzProperty = beanClass.getProperty("xyz")
        doAssertEquals(beanClass.canReadProperty("xyz"), false)
        xyzProperty.get().setValue(a, "8888")
        doAssertEquals(wwwProperty.get().getValue(a), "8888")
        doExpectThrowable(UnsupportedOperationException::class.java) {
            xyzProperty.get().getValue(a)
        }

        val propertyNames = beanClass.allProperties.map { e ->
            e.key
        }.toSet()
        doAssertEquals(propertyNames, setOf("class", "read", "www", "xyz"))
        val readablePropertyNames = beanClass.readableProperties.map { e ->
            e.key
        }.toSet()
        doAssertEquals(readablePropertyNames, setOf("class", "read", "www"))
        val writeablePropertyNames = beanClass.writeableProperties.map { e ->
            e.key
        }.toSet()
        doAssertEquals(writeablePropertyNames, setOf("www", "xyz"))

        val realMethod = A::class.java.getMethod("someMethod", String::class.java)
        doAssertEquals(beanClass.allMethods.contains(SignatureHelper.signMethod(realMethod)), true)
        val someMethod = beanClass.getMethod("someMethod", String::class.java)
        val someMethodSignature = beanClass.getMethodBySignature(SignatureHelper.signMethod(realMethod))
        doAssertEquals(someMethod.get().method, realMethod)
        doAssertEquals(someMethodSignature.get().method, realMethod)
        doAssertEquals(someMethod.get(), someMethodSignature.get())
        doAssertEquals(someMethod.get().invoke(a, "a"), "aa")
    }

    val customBeanResolver = BeanResolver.newBuilder()
        .addHandler(object : BeanResolverHandler {
            override fun supportBean(beanClass: Class<*>): Boolean {
                return !Int::class.java.equals(beanClass);
            }

            override fun resolve(beanClass: Class<*>): BeanClass {
                return BeanClassSupport.newBuilder()
                    .setType(beanClass)
                    .setProperties(mapOf("1" to object : BeanProperty {

                        private var value: Int? = 1

                        override fun setValue(bean: Any, value: Any?) {
                            this.value = Integer.parseInt(value.toString())
                        }

                        override fun getGenericType(): Type {
                            return type
                        }

                        override fun getReadMethod(): Optional<Method> {
                            return Optional.empty()
                        }

                        override fun isReadable(): Boolean {
                            return true
                        }

                        override fun isWriteable(): Boolean {
                            return true
                        }

                        override fun getWriteMethod(): Optional<Method> {
                            return Optional.empty()
                        }

                        override fun getName(): String {
                            return "1"
                        }

                        override fun getType(): Class<*> {
                            return Int::class.java
                        }

                        override fun getValue(bean: Any): Any? {
                            return this.value
                        }
                    }))
                    .build()
            }
        })
        .build()

    @Test
    fun testCustomResolver() {
        val customBeanClass = customBeanResolver.resolve(Object::class.java)
        doAssertEquals(customBeanClass.getProperty("1").get().getValue(""), 1)
        customBeanClass.getProperty("1").get().setValue("1", "222")
        doAssertEquals(customBeanClass.getProperty("1").get().getValue(""), 222)

        doExpectThrowable(UnsupportedOperationException::class.java) {
            customBeanResolver.resolve(1.javaClass)
        }
    }

    @Test
    fun testEmptyResolver() {
        val emptyBeanResolver = BeanResolver.newBuilder()
            .addHandler(BeanResolverHandler.DEFAULT)
            .build()
        val byDefault = BeanResolver.DEFAULT.resolve(Any::class.java)
        val byEmpty = emptyBeanResolver.resolve(Any::class.java)
        doAssertEquals(byEmpty.allProperties, byDefault.allProperties)

        val propertyByDefault = byDefault.getProperty("class").get()
        val propertyByEmpty = byEmpty.getProperty("class").get()
        doAssertEquals(propertyByEmpty, propertyByDefault)
        doAssertEquals(propertyByEmpty == propertyByDefault, true)
        doAssertEquals(propertyByEmpty.name, propertyByDefault.name)
        doAssertEquals(propertyByEmpty.type, propertyByDefault.type)
        doAssertEquals(propertyByEmpty.genericType, propertyByDefault.genericType)
        doAssertEquals(propertyByEmpty.readMethod, propertyByDefault.readMethod)
        doAssertEquals(propertyByEmpty.writeMethod, propertyByDefault.writeMethod)

        val methodByDefault = byDefault.getMethod("getClass").get()
        val methodByEmpty = byEmpty.getMethod("getClass").get()
        doAssertEquals(methodByEmpty, methodByDefault)
        doAssertEquals(methodByEmpty == methodByDefault, true)
        doAssertEquals(methodByEmpty.name, methodByDefault.name)
        doAssertEquals(methodByEmpty.returnType, methodByDefault.returnType)
        doAssertEquals(methodByEmpty.genericReturnType, methodByDefault.genericReturnType)
        doAssertEquals(methodByEmpty.parameterCount, methodByDefault.parameterCount)
        doAssertEquals(methodByEmpty.parameterTypes, methodByDefault.parameterTypes)
        doAssertEquals(methodByEmpty.genericParameterTypes, methodByDefault.genericParameterTypes)
    }

    class A {

        val read: String = "read"

        var www: String? = null
        fun setXyz(www: String?) {
            this.www = www
        }

        fun someMethod(argument: String): String {
            return argument + argument
        }
    }
}
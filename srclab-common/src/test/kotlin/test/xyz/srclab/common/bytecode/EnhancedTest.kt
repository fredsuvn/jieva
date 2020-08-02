//package test.xyz.srclab.common.bytecode
//
//import org.testng.annotations.Test
//import xyz.srclab.common.bean.BeanHelper
//import xyz.srclab.common.bytecode.EnhancedClass
//import xyz.srclab.common.bytecode.provider.ByteCodeProvider
//import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider
//import xyz.srclab.common.bytecode.provider.spring.SpringByteCodeProvider
//import xyz.srclab.test.doAssertEquals
//
///**
// * @author sunqian
// */
//object EnhancedTest {
//
//    @Test
//    fun testProxy() {
//        val proxyClass = EnhancedClass.newBuilder(A1::class.java)
//            .addInterfaces(listOf(B::class.java))
//            .overrideMethod(
//                "hello", arrayOf()
//            ) { `object`, args, method, superInvoker -> "proxy: " + superInvoker.invoke(`object`, args) }
//            .overrideMethod(
//                "bb", arrayOf()
//            ) { `object`, args, method, superInvoker -> "proxy: bb" }
//            .build()
//        val instance = proxyClass.newInstance()
//        doAssertEquals(instance.hello(), "proxy: world")
//        doAssertEquals(
//            BeanHelper.resolveBean(instance.javaClass).getMethod("bb").invoke(instance), "proxy: bb"
//        )
//    }
//
//    @Test
//    fun testWithProvider() {
//        doTestProxy(CglibByteCodeProvider.INSTANCE, A3::class.java)
//        doTestProxy(SpringByteCodeProvider.INSTANCE, A2::class.java)
//    }
//
//    private fun <T : Any> doTestProxy(provider: ByteCodeProvider, baseClass: Class<T>) {
//        val proxyClass = provider.newEnhancedClassBuilder(baseClass)
//            .addInterfaces(B::class.java)
//            .overrideMethod(
//                "hello", arrayOf()
//            ) { `object`, args, method, superInvoker -> "proxy: " + superInvoker.invoke(`object`, args) }
//            .overrideMethod(
//                "bb", arrayOf()
//            ) { `object`, args, method, superInvoker -> "proxy: bb" }
//            .build()
//        val instance = proxyClass.newInstance()
//        doAssertEquals(
//            BeanHelper.resolveBean(instance.javaClass).getMethod("hello").invoke(instance),
//            "proxy: world"
//        )
//        doAssertEquals(
//            BeanHelper.resolveBean(instance.javaClass).getMethod("bb").invoke(instance),
//            "proxy: bb"
//        )
//    }
//
//    open class A1() {
//
//        constructor(str: String) : this() {
//            println(str)
//        }
//
//        open fun hello(): String {
//            return "world"
//        }
//    }
//
//    open class A2() {
//
//        constructor(str: String) : this() {
//            println(str)
//        }
//
//        open fun hello(): String {
//            return "world"
//        }
//    }
//
//    open class A3() {
//
//        constructor(str: String) : this() {
//            println(str)
//        }
//
//        open fun hello(): String {
//            return "world"
//        }
//    }
//
//    interface B {
//        fun bb(): String
//    }
//}
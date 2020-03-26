package test.xyz.srclab.common.proxy

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssert
import test.xyz.srclab.common.model.SomeInterface1
import test.xyz.srclab.common.model.SomeSomeClass1
import xyz.srclab.common.lang.Ref
import xyz.srclab.common.proxy.ClassProxy
import xyz.srclab.common.reflect.MethodBody

object ProxyTest {

    @Test
    fun testInterfaceProxy() {
        val messageRef = Ref.with("")
        val proxy = ClassProxy.newBuilder(SomeInterface1::class.java)
            .proxyMethod("some1InterfaceFun", arrayOf(),
                MethodBody<Any> { `object`, method, args, invoker ->
                    println("proxy some1InterfaceFun!")
//                    invoker.invoke(`object`)
                    messageRef.set("xxxx")
                })
            .build()
        proxy.newInstance().some1InterfaceFun()
        doAssert(messageRef.get(), "xxxx")
    }

    @Test
    fun testClassProxy() {
        val messageRef = Ref.with("")
        val proxy = ClassProxy.newBuilder(SomeSomeClass1::class.java)
            .proxyMethod("someSome1PublicFun", arrayOf(),
                MethodBody<Any> { `object`, method, args, invoker ->
                    println("proxy someSome1PublicFun!")
                    invoker.invoke(`object`)
                    messageRef.set("xxxx")
                })
            .build()
        proxy.newInstance().someSome1PublicFun()
        doAssert(messageRef.get(), "xxxx")
    }
}
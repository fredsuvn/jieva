//package test.xyz.srclab.common.reflect
//
//import org.testng.Assert
//import org.testng.annotations.Test
//import test.xyz.srclab.common.doAssert
//import test.xyz.srclab.common.model.BoundModel
//import test.xyz.srclab.common.model.SomeSomeClass1
//import xyz.srclab.common.reflect.ReflectHelper
//import xyz.srclab.common.reflect.SignatureHelper
//import java.lang.reflect.Type
//
//object ReflectTest {
//
//    @Test
//    fun testAllMethods() {
//        val all = ReflectHelper
//            .getAllMethods(SomeSomeClass1::class.java)
//            .map { m -> SignatureHelper.signatureMethodWithClassAndMethodName(m) }
//        val expectedAll = mutableListOf(
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1Static()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PublicFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1ProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.some1InterfaceFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.parentProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1String()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1String(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Int()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Int(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome2Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome2Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PublicFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1ProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentString()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentString(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentInt()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentInt(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.parentPublicFun()V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.parentProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.parentPrivateFun()V",
//            "MLjava/lang/Object;.finalize()V",
//            "MLjava/lang/Object;.wait()V",
//            "MLjava/lang/Object;.wait(JI)V",
//            "MLjava/lang/Object;.wait(J)V",
//            "MLjava/lang/Object;.equals(Ljava/lang/Object;)Z",
//            "MLjava/lang/Object;.toString()Ljava/lang/String;",
//            "MLjava/lang/Object;.hashCode()I",
//            "MLjava/lang/Object;.getClass()Ljava/lang/Class;",
//            "MLjava/lang/Object;.clone()Ljava/lang/Object;",
//            "MLjava/lang/Object;.notify()V",
//            "MLjava/lang/Object;.notifyAll()V",
//            "MLjava/lang/Object;.registerNatives()V"
//        )
//        println("actual all: $all")
//        println("expected all: $expectedAll")
//        Assert.assertEquals(HashSet(all), HashSet(expectedAll))
//    }
//
//    @Test
//    fun testOverrideMethods() {
//        val overrides = ReflectHelper
//            .getOverrideableMethods(SomeSomeClass1::class.java)
//            .map { m -> SignatureHelper.signatureMethodWithClassAndMethodName(m) }
//        val expectedOverrides = mutableListOf(
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PublicFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1ProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.some1InterfaceFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.parentProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1String()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1String(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Int()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Int(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome2Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome2Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PublicFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1ProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentString()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentString(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentInt()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentInt(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.parentPublicFun()V",
////            "MLtest/xyz/srclab/common/model/ParentClass;.parentProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/ParentClass;.parentPrivateFun()V",
//            "MLjava/lang/Object;.finalize()V",
////            "MLjava/lang/Object;.wait()V",
////            "MLjava/lang/Object;.wait(JI)V",
////            "MLjava/lang/Object;.wait(J)V",
//            "MLjava/lang/Object;.equals(Ljava/lang/Object;)Z",
//            "MLjava/lang/Object;.toString()Ljava/lang/String;",
//            "MLjava/lang/Object;.hashCode()I",
////            "MLjava/lang/Object;.getClass()Ljava/lang/Class;",
//            "MLjava/lang/Object;.clone()Ljava/lang/Object;"
////            "MLjava/lang/Object;.notify()V",
////            "MLjava/lang/Object;.notifyAll()V",
////            "MLjava/lang/Object;.registerNatives()V"
//        )
//        println("actual overrides: $overrides")
//        println("expected overrides: $expectedOverrides")
//        Assert.assertEquals(HashSet(overrides), HashSet(expectedOverrides))
//    }
//
//    @Test
//    fun testPublicStaticMethods() {
//        val publicStatic = ReflectHelper
//            .getPublicStaticMethods(SomeSomeClass1::class.java)
//            .map { m -> SignatureHelper.signatureMethodWithClassAndMethodName(m) }
//        val expectedPublicStatic = mutableListOf(
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1Static()V"
//        )
//        println("actual all: $publicStatic")
//        println("expected all: $expectedPublicStatic")
//        Assert.assertEquals(HashSet(publicStatic), HashSet(expectedPublicStatic))
//    }
//
//    @Test
//    fun testPublicNonStaticMethods() {
//        val publicNonStatic = ReflectHelper
//            .getPublicNonStaticMethods(SomeSomeClass1::class.java)
//            .map { m -> SignatureHelper.signatureMethodWithClassAndMethodName(m) }
//        val expectedPublicNonStatic = mutableListOf(
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PublicFun()V",
////            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1ProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.someSome1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.some1InterfaceFun()V",
////            "MLtest/xyz/srclab/common/model/SomeSomeClass1;.parentProtectedFun()V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1String()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1String(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Int()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Int(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome1Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome1Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.getSome2Some()Ltest/xyz/srclab/common/model/SomeClass2;",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.setSome2Some(Ltest/xyz/srclab/common/model/SomeClass2;)V",
//            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PublicFun()V",
////            "MLtest/xyz/srclab/common/model/SomeClass1;.some1ProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/SomeClass1;.some1PrivateFun()V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentString()Ljava/lang/String;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentString(Ljava/lang/String;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.getParentInt()Ljava/lang/Integer;",
//            "MLtest/xyz/srclab/common/model/ParentClass;.setParentInt(Ljava/lang/Integer;)V",
//            "MLtest/xyz/srclab/common/model/ParentClass;.parentPublicFun()V",
////            "MLtest/xyz/srclab/common/model/ParentClass;.parentProtectedFun()V",
////            "MLtest/xyz/srclab/common/model/ParentClass;.parentPrivateFun()V",
////            "MLjava/lang/Object;.finalize()V",
//            "MLjava/lang/Object;.wait()V",
//            "MLjava/lang/Object;.wait(JI)V",
//            "MLjava/lang/Object;.wait(J)V",
//            "MLjava/lang/Object;.equals(Ljava/lang/Object;)Z",
//            "MLjava/lang/Object;.toString()Ljava/lang/String;",
//            "MLjava/lang/Object;.hashCode()I",
//            "MLjava/lang/Object;.getClass()Ljava/lang/Class;",
////            "MLjava/lang/Object;.clone()Ljava/lang/Object;"
//            "MLjava/lang/Object;.notify()V",
//            "MLjava/lang/Object;.notifyAll()V"
////            "MLjava/lang/Object;.registerNatives()V"
//        )
//        println("actual all: $publicNonStatic")
//        println("expected all: $expectedPublicNonStatic")
//        Assert.assertEquals(HashSet(publicNonStatic), HashSet(expectedPublicNonStatic))
//    }
//
//    @Test
//    fun testFindGenericSupperClass() {
//        open class A<T>
//        open class B : A<String>()
//        open class C : B()
//        open class D : C()
//
//        val genericA = ReflectHelper.findGenericSuperclass(D::class.java, A::class.java)
//        println(genericA)
//        doAssert(genericA?.javaClass is Type, true)
//        doAssert(ReflectHelper.getClass(genericA), A::class.java)
//
//        val genericB = ReflectHelper.findGenericSuperclass(D::class.java, B::class.java)
//        println(genericB)
//        doAssert(genericB?.javaClass is Class, true)
//        doAssert(ReflectHelper.getClass(genericB), B::class.java)
//    }
//
//    @Test
//    fun testBound() {
//        BoundModel::class.java.declaredMethods.forEach { method ->
//            println("method: ${method.name}")
//            println("generic return type: ${method.genericReturnType}")
//            println("return type: ${method.returnType}")
//            doAssert(ReflectHelper.getClass((method.genericReturnType)), method.returnType)
//            println("generic parameter type: ${method.genericParameterTypes[0]}")
//            println("parameter type: ${method.parameterTypes[0]}")
//            doAssert(ReflectHelper.getClass((method.genericParameterTypes[0])), method.parameterTypes[0])
//        }
//    }
//}
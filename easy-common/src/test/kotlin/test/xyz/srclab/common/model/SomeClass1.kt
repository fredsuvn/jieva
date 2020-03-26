package test.xyz.srclab.common.model

open class SomeClass1 : ParentClass() {

    open var some1String: String? = null
    open var some1Int: Int? = null
    open var some1Some: SomeClass2? = null
    open var some2Some: SomeClass2? = null

    open fun some1PublicFun() {

    }

    protected open fun some1ProtectedFun() {

    }

    private fun some1PrivateFun() {

    }
}
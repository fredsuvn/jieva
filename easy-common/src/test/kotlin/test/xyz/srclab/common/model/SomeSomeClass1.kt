package test.xyz.srclab.common.model

open class SomeSomeClass1 : SomeClass1(), SomeInterface1 {

    open fun someSome1PublicFun() {

    }

    protected open fun someSome1ProtectedFun() {

    }

    private fun someSome1PrivateFun() {

    }

    override fun some1InterfaceFun() {

    }

    override fun parentProtectedFun() {

    }

    companion object {
        @JvmStatic
        fun someSome1Static() {

        }
    }
}
package xyz.srclab.common.egg

import xyz.srclab.common.base.sleep

open class HelloEgg : Egg {

    override fun readme() {
        println("Try:")
        println("    Egg egg = Egg.pick(\"${HelloEgg::class.java.name}\")")
        println("    egg.hatchOut(\"Your Name\")")
    }

    override fun hatchOut(magic: Any?) {
        sleep(1000)
        println(".")
        println("Hello, $magic!")
    }
}
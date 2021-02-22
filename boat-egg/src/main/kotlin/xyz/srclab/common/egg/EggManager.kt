package xyz.srclab.common.egg

interface EggManager {

    @Throws(EggNotFoundException::class)
    fun pick(name: CharSequence): Egg
}
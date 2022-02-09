package xyz.srclab.common.egg

import xyz.srclab.common.reflect.classForName
import xyz.srclab.common.reflect.newInst

interface Egg {

    fun readme()

    fun hatchOut() {
        hatchOut(null)
    }

    fun hatchOut(magic: Any?)

    companion object {

        @JvmStatic
        fun pick(egg: String): Egg {
            try {
                return egg.classForName<Egg>().newInst()
            } catch (e: Exception) {
                if (e is NoSuchEggException) {
                    throw e
                }
                throw NoSuchEggException(egg, e)
            }
        }
    }
}
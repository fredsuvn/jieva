package xyz.srclab.common.egg

import xyz.srclab.common.base.loadClass
import xyz.srclab.common.reflect.toInstance

/**
 * @author sunqian
 */
interface Egg {

    fun hatchOut(spell: CharSequence)

    companion object {

        @JvmStatic
        fun pick(one: CharSequence): Egg {
            return one.loadClass<Egg>().toInstance()
        }
    }
}
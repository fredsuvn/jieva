package xyz.srclab.common.egg

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.loadClass

/**
 * Egg interface.
 *
 * @author sunqian
 */
interface Egg {

    @get:JvmName("readme")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val readme: String

    /**
     * Hatches out with spell!
     */
    @JvmDefault
    fun hatchOut(spell: String) {
        hatchOut(spell, emptyMap())
    }

    /**
     * Hatches out with spell and feed!
     */
    fun hatchOut(spell: String, feed: Map<Any, Any>)

    companion object {

        /**
         * Picks an egg!
         */
        @JvmStatic
        fun pick(egg: String): Egg {
            try {
                return egg.loadClass<Egg>().newInstance()
            } catch (e: Exception) {
                if (e is EggNotFoundException) {
                    throw e
                }
                throw EggNotFoundException(egg, e)
            }
        }
    }
}
package xyz.srclab.common.egg

import xyz.srclab.common.lang.loadClass

/**
 * Egg interface.
 *
 * @author sunqian
 */
interface Egg {

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
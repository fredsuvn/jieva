package xyz.srclab.common.egg

import xyz.srclab.common.base.checkArgument
import xyz.srclab.common.codec.Codec.Companion.decodeBase64String
import xyz.srclab.common.egg.v0.OSpaceBattle

/**
 * @author sunqian
 */
interface Egg {

    fun hatchOut(spell: CharSequence)

    companion object {

        @JvmStatic
        fun pickOne(name: CharSequence): Egg {
            val func = eggMap[name] ?: throw IllegalArgumentException("No egg called $name!")
            return func()
        }

        private val eggMap = HashMap<String, () -> Egg>()

        /*
         * Init eggs.
         */
        init {
            eggMap["Hello Egg"] = {
                SampleEgg()
            }
            eggMap["O Space Battle"] = {
                OSpaceBattle()
            }
        }

        private class SampleEgg : Egg {

            override fun hatchOut(spell: CharSequence) {
                checkSpell(spell)

                println(
                    """
                    Well,
                    this is a sample egg,
                    if you really want to a true egg,
                    please pick by a true name,
                    and hatch out with a true spell ~
                """.trimIndent()
                )
            }

            private fun checkSpell(spell: CharSequence) {
                checkArgument(spell.decodeBase64String() == "快跑！这里没有加班费！", "Wrong Spell!")
            }
        }
    }
}
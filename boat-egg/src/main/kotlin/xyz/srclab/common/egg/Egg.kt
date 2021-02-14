package xyz.srclab.common.egg

import xyz.srclab.common.base.checkArgument
import xyz.srclab.common.base.loadStringResource
import xyz.srclab.common.codec.Codec.Companion.decodeBase64String
import xyz.srclab.common.reflect.toInstance

/**
 * @author sunqian
 */
interface Egg {

    val readmeMd: String

    val readMeAdoc: String

    fun hatchOut(spell: CharSequence)

    companion object {

        @JvmStatic
        fun pickOne(name: CharSequence): Egg {
            val func = eggMap[name] ?: throw IllegalArgumentException("No egg called $name!")
            return func()
        }

        /*
         * Egg management:
         * Loads SampleEgg;
         * Scans package v0 - vx, find xyz.srclab.common.egg.vx.VxPreEgg, to load all egg infos.
         */
        private val eggMap = HashMap<String, () -> Egg>()

        /*
         * Init eggs.
         */
        init {
            eggMap["Hello Egg"] = {
                SampleEgg()
            }
            var i = 0
            while (true) {
                try {
                    val preEgg = "xyz.srclab.common.egg.v$i.V${i}PreEgg".toInstance<PreEgg>()
                    eggMap[preEgg.eggName] = {
                        preEgg.eggClassName.toInstance()
                    }
                    i++
                } catch (e: Exception) {
                    break
                }
            }
        }

        private class SampleEgg : Egg {

            private val tip = "eggs/sample.readme".loadStringResource()

            override val readmeMd: String = tip
            override val readMeAdoc: String = tip

            override fun hatchOut(spell: CharSequence) {
                checkSpell(spell)

                println(tip)
            }

            private fun checkSpell(spell: CharSequence) {
                checkArgument(spell.decodeBase64String() == "快跑！这里没有加班费！", "Wrong Spell!")
            }
        }
    }
}
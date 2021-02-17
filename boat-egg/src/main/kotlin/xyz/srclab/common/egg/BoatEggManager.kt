package xyz.srclab.common.egg

import xyz.srclab.common.reflect.shortName
import xyz.srclab.common.reflect.toInstance

object BoatEggManager : EggManager {

    /*
     * Egg management:
     * Loads SampleEgg;
     * Scans package v0 - vx, find xyz.srclab.common.egg.vx.VxEggMeta, to load all egg infos.
     */
    private val eggMap = HashMap<String, () -> Egg>()

    init {
        eggMap["Hello, Boat Egg!"] = {
            SampleEgg
        }
        var i = 0
        while (true) {
            try {
                val preEgg = "xyz.srclab.common.egg.v$i.V${i}${EggMeta::class.java.shortName}".toInstance<EggMeta>()
                eggMap[preEgg.eggName] = {
                    preEgg.eggClassName.toInstance()
                }
                i++
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun pick(name: CharSequence): Egg {
        val func = eggMap[name] ?: throw EggNotFoundException(name)
        return func()
    }

    private object SampleEgg : Egg {

        private val tip = """
            Well~
            this is just a sample egg,
            if you really want to a true egg,
            please pick by a true name,
            and hatch out with a true spell~

            Of course,
            you don't know name and spell,
            but I hear there are a lot of secrets in that maze[https://github.com/srclab-projects/maze?].
            Maybe you can find out there~
        """.trimIndent()

        override fun hatchOut(spell: CharSequence) {
            println(tip)
        }
    }
}
package xyz.srclab.common.egg

import xyz.srclab.common.egg.nest.o.OBattle

/**
 * Boat Egg Manager.
 */
object BoatEggManager : EggManager {

    private val eggMap = HashMap<String, () -> Egg>()

    init {
        eggMap["Hello, Boat Egg!"] = {
            SampleEgg
        }
        eggMap["O Battle"] = {
            OBattle()
        }
    }

    override fun pick(name: CharSequence): Egg {
        val eggGen = eggMap[name] ?: throw EggNotFoundException(name)
        return eggGen()
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
            but I hear there are a lot of secrets in that maze[https://github.com/srclab-projects/maze],
            and a secret code in Boat,
            good luck~
        """.trimIndent()

        override val shell: String = "This shell is spotless and white."

        override fun hatchOut(spell: CharSequence) {
            println(tip)
        }
    }
}
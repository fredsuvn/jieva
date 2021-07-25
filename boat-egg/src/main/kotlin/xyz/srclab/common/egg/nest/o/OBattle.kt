package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.Boat
import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OBattle : Egg {

    override val readme: String
        get() = TODO("Not yet implemented")

    override fun hatchOut(spell: String, feed: Map<Any, Any>) {
        for (secretCode in Boat.secretCodes) {
            if (secretCode == spell) {
                go()
                return
            }
        }
        throw IAmSevenNotTaro()
    }

    private fun go() {
        OView().isVisible = true
    }

    private class IAmSevenNotTaro : RuntimeException(
        """
        I AM ULTRA SEVEN, NOT ULTRAMAN TARO, YOU SHOULD THANK FOR SEVEN NOT TARO, FUCK YOU!
              fﾆヽ
        　　　 |_||
        　　　 |= |
        　　　 |_ |
        　　/⌒|~ |⌒i-、
        　 /|　|　|　| ｜
        　｜(　(　(　( ｜
        　｜　　　　　 ｜ 　
        　 ＼　　　　　/
        　　 ＼　　　 　
        """.trimIndent()
    )
}
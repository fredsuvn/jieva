package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OBattle : Egg {

    override fun hatchOut(spell: CharSequence) {
        when (spell.toString()) {
            "Thank you, Taro.",
            "谢谢你，泰罗。",
            -> run()
            else -> throw IAmSevenNotTaro()
        }
    }

    private fun run() {
        OSpaceView(OSpaceConfig()).isVisible = true
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
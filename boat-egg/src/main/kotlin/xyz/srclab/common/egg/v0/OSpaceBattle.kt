package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OSpaceBattle : Egg {

    override fun hatchOut(spell: CharSequence) {
        when (spell.toString()) {
            "Thank you, Taro.",
            "谢谢你，泰罗。",
            -> run()
            else -> throw ImSevenNotTaro()
        }
    }

    private fun run() {
        OSpaceView(OSpaceConfig()).isVisible = true
    }

    private class ImSevenNotTaro : RuntimeException(
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
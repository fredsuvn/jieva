package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OSpaceBattle : Egg {

    override fun hatchOut(spell: CharSequence) {
        val view = OSpaceView(OSpaceConfig(), OSpaceLogger())
        view.isVisible = true
        //view.paint(view.graphics)
//        view.background = Color.GREEN
//        view.graphics.color = Color.GREEN
//        view.graphics.drawOval(0, 0, 10, 10)
    }
}